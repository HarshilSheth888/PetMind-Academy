package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArticlesData
import com.example.data.BehaviorDecoderData
import com.example.data.TrainingGuidesData
import com.example.data.db.AppDatabase
import com.example.data.model.ArticleProgressEntity
import com.example.data.model.MilestoneEntity
import com.example.data.model.PetEntity
import com.example.data.model.SkillProgressEntity
import com.example.data.model.TrainingLogEntity
import com.example.data.repository.PetMindRepository
import com.example.model.Article
import com.example.model.ArticleCategory
import com.example.model.BehaviorSignal
import com.example.model.GuideDifficulty
import com.example.model.TrainingGuide
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
  val selectedCategories: Set<ArticleCategory> = emptySet(),
  val searchQuery: String = "",
  val showBookmarksOnly: Boolean = false,
  val selectedSpecies: Set<String> = emptySet(), // "Dog", "Cat"
)

data class TrainingUiState(
  val selectedDifficulties: Set<GuideDifficulty> = emptySet(),
  val selectedCategories: Set<String> = emptySet(),
  val searchQuery: String = "",
  val selectedSpecies: Set<String> = emptySet(),
)

data class DecoderUiState(
  val selectedSpecies: String = "Dog", // "Dog", "Cat"
  val selectedBodyPart: String = "All", // "All", "Eyes", "Ears", "Tail", "Mouth/Face", "Body Posture"
  val searchQuery: String = ""
)

class PetMindViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  val repository = PetMindRepository(
    petDao = database.petDao(),
    trainingDao = database.trainingDao(),
    articleDao = database.articleDao(),
    milestoneDao = database.milestoneDao()
  )

  init {
    viewModelScope.launch {
      repository.ensureDefaultDataSeeded()
    }
  }

  // --- Theme Mode State ---
  private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
  val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

  fun cycleThemeMode() {
    _themeMode.value = when (_themeMode.value) {
      ThemeMode.SYSTEM -> ThemeMode.LIGHT
      ThemeMode.LIGHT -> ThemeMode.DARK
      ThemeMode.DARK -> ThemeMode.SYSTEM
    }
  }

  fun setThemeMode(mode: ThemeMode) {
    _themeMode.value = mode
  }

  // --- Pets State ---
  val allPets: StateFlow<List<PetEntity>> = repository.allPets
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedPetId = MutableStateFlow<Long?>(null)
  @Suppress("unused")
  val selectedPetId: StateFlow<Long?> = _selectedPetId.asStateFlow()

  val activePet: StateFlow<PetEntity?> = combine(allPets, _selectedPetId) { pets, selId ->
    if (pets.isEmpty()) null
    else pets.firstOrNull { it.id == selId } ?: pets.first()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  fun selectPet(petId: Long) {
    _selectedPetId.value = petId
  }

  fun addNewPet(name: String, species: String, breed: String, ageMonths: Int, gender: String, tags: String, notes: String, avatarIndex: Int) {
    viewModelScope.launch {
      val newId = repository.insertPet(
        PetEntity(
          name = name,
          species = species,
          breed = breed,
          ageMonths = ageMonths,
          gender = gender,
          personalityTags = tags,
          notes = notes,
          avatarIndex = avatarIndex
        )
      )
      _selectedPetId.value = newId
    }
  }

  fun updatePet(pet: PetEntity) {
    viewModelScope.launch {
      repository.updatePet(pet)
    }
  }

  @Suppress("unused")
  fun deletePet(pet: PetEntity) {
    viewModelScope.launch {
      repository.deletePet(pet)
      _selectedPetId.value = null
    }
  }

  // --- Articles & Knowledge State ---
  private val _homeState = MutableStateFlow(HomeUiState())
  val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

  val articleProgressList: StateFlow<List<ArticleProgressEntity>> = repository.allArticleProgress
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @Suppress("unused")
  val readCount: StateFlow<Int> = repository.readArticlesCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  @Suppress("unused")
  val bookmarkCount: StateFlow<Int> = repository.bookmarkedArticlesCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val filteredArticles: StateFlow<List<Article>> = combine(
    _homeState,
    articleProgressList,
    activePet
  ) { state, progressList, pet ->
    val progressMap = progressList.associateBy { it.articleId }
    ArticlesData.articles.filter { article ->
      val matchesCategory = state.selectedCategories.isEmpty() || (article.category in state.selectedCategories)
      val matchesSpecies = article.matchesSpecies(state.selectedSpecies, pet)
      val matchesSearch = state.searchQuery.isBlank() ||
        article.title.contains(state.searchQuery, ignoreCase = true) ||
        article.summary.contains(state.searchQuery, ignoreCase = true) ||
        article.corePsychologyInsight.contains(state.searchQuery, ignoreCase = true)
      val matchesBookmark = !state.showBookmarksOnly || (progressMap[article.id]?.isBookmarked == true)

      matchesCategory && matchesSpecies && matchesSearch && matchesBookmark
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ArticlesData.articles)

  fun setArticleFilters(categories: Set<ArticleCategory>, species: Set<String>, showBookmarks: Boolean) {
    _homeState.value = _homeState.value.copy(
      selectedCategories = categories,
      selectedSpecies = species,
      showBookmarksOnly = showBookmarks
    )
  }

  fun setArticleSearch(query: String) {
    _homeState.value = _homeState.value.copy(searchQuery = query)
  }

  fun toggleArticleBookmark(articleId: String) {
    viewModelScope.launch {
      repository.toggleBookmark(articleId)
    }
  }

  fun markArticleRead(articleId: String, quizScore: Int? = null) {
    viewModelScope.launch {
      repository.markArticleRead(articleId, quizScore)
    }
  }

  // --- Training Guides State ---
  private val _trainingState = MutableStateFlow(TrainingUiState())
  val trainingState: StateFlow<TrainingUiState> = _trainingState.asStateFlow()

  val filteredGuides: StateFlow<List<TrainingGuide>> = combine(
    _trainingState,
    activePet
  ) { state, pet ->
    TrainingGuidesData.guides.filter { guide ->
      val matchesDifficulty = state.selectedDifficulties.isEmpty() || (guide.difficulty in state.selectedDifficulties)
      val matchesCategory = state.selectedCategories.isEmpty() || state.selectedCategories.any { it.equals(guide.category, ignoreCase = true) }
      val matchesSpecies = guide.matchesSpecies(state.selectedSpecies, pet)
      val matchesSearch = state.searchQuery.isBlank() ||
        guide.title.contains(state.searchQuery, ignoreCase = true) ||
        guide.shortDescription.contains(state.searchQuery, ignoreCase = true) ||
        guide.whyItWorksPsychologically.contains(state.searchQuery, ignoreCase = true)

      matchesDifficulty && matchesCategory && matchesSpecies && matchesSearch
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingGuidesData.guides)

  fun setTrainingFilters(difficulties: Set<GuideDifficulty>, categories: Set<String>, species: Set<String>) {
    _trainingState.value = _trainingState.value.copy(
      selectedDifficulties = difficulties,
      selectedCategories = categories,
      selectedSpecies = species
    )
  }

  fun setGuideSearch(query: String) {
    _trainingState.value = _trainingState.value.copy(searchQuery = query)
  }

  // --- Active Pet Training Progress, Logs, & Milestones ---
  @OptIn(ExperimentalCoroutinesApi::class)
  val currentPetLogs: StateFlow<List<TrainingLogEntity>> = activePet.flatMapLatest { pet ->
    if (pet != null) repository.getLogsForPet(pet.id)
    else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val currentPetSkillProgress: StateFlow<List<SkillProgressEntity>> = activePet.flatMapLatest { pet ->
    if (pet != null) repository.getSkillProgressForPet(pet.id)
    else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val currentPetMilestones: StateFlow<List<MilestoneEntity>> = activePet.flatMapLatest { pet ->
    if (pet != null) repository.getMilestonesForPet(pet.id)
    else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val currentPetTotalTime: StateFlow<Int> = activePet.flatMapLatest { pet ->
    if (pet != null) repository.getTotalTrainingTimeSeconds(pet.id)
    else flowOf(0)
  }.combine(flowOf(0)) { dbTime, _ ->
    dbTime ?: 0
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  @OptIn(ExperimentalCoroutinesApi::class)
  val currentPetSessionCount: StateFlow<Int> = activePet.flatMapLatest { pet ->
    if (pet != null) repository.getTotalSessionCount(pet.id)
    else flowOf(0)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  fun logSession(
    guideId: String,
    guideTitle: String,
    durationSeconds: Int,
    repetitions: Int,
    successCount: Int,
    difficultyRating: Int,
    notes: String,
    currentStepCompleted: Int,
    totalSteps: Int
  ) {
    val pet = activePet.value ?: return
    viewModelScope.launch {
      repository.logTrainingSession(
        petId = pet.id,
        guideId = guideId,
        guideTitle = guideTitle,
        durationSeconds = durationSeconds,
        repetitions = repetitions,
        successCount = successCount,
        difficultyRating = difficultyRating,
        notes = notes,
        currentStepCompleted = currentStepCompleted,
        totalSteps = totalSteps
      )
    }
  }

  fun toggleMilestone(milestone: MilestoneEntity) {
    viewModelScope.launch {
      repository.unlockMilestone(milestone)
    }
  }

  // --- Behavior Decoder State ---
  private val _decoderState = MutableStateFlow(DecoderUiState())
  val decoderState: StateFlow<DecoderUiState> = _decoderState.asStateFlow()

  val filteredSignals: StateFlow<List<BehaviorSignal>> = _decoderState.combine(flowOf(BehaviorDecoderData.signals)) { state, allSignals ->
    allSignals.filter { signal ->
      val matchesSpecies = signal.species.equals(state.selectedSpecies, ignoreCase = true)
      val matchesBodyPart = state.selectedBodyPart == "All" || signal.bodyPart.equals(state.selectedBodyPart, ignoreCase = true)
      val matchesSearch = state.searchQuery.isBlank() ||
        signal.observationTitle.contains(state.searchQuery, ignoreCase = true) ||
        signal.description.contains(state.searchQuery, ignoreCase = true) ||
        signal.whatItMeans.contains(state.searchQuery, ignoreCase = true)

      matchesSpecies && matchesBodyPart && matchesSearch
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BehaviorDecoderData.signals)

  fun setDecoderSpecies(species: String) {
    _decoderState.value = _decoderState.value.copy(selectedSpecies = species)
  }

  fun setDecoderBodyPart(bodyPart: String) {
    _decoderState.value = _decoderState.value.copy(selectedBodyPart = bodyPart)
  }

  fun setDecoderSearch(query: String) {
    _decoderState.value = _decoderState.value.copy(searchQuery = query)
  }
}
