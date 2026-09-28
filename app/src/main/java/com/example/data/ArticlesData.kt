package com.example.data

import com.example.model.Article
import com.example.model.ArticleCategory
import com.example.model.ArticleSection
import com.example.model.QuizQuestion

object ArticlesData {
  val articles: List<Article> = listOf(
    Article(
      id = "art_body_language_canine",
      title = "The Canine Emotional Spectrum: Reading Subtle Body Language",
      subtitle = "Decoding calming signals, displacement behaviors, and emotional thresholds before reactivity starts.",
      category = ArticleCategory.PSYCHOLOGY,
      speciesTarget = "Dog",
      readTimeMinutes = 5,
      authorName = "Dr. Elena Rostova, PhD",
      authorCredentials = "Certified Applied Animal Behaviorist (CAAB)",
      heroImageResName = "img_hero_pet_mentality",
      summary = "Dogs communicate primarily through micro-movements of their eyes, ears, commissures, and tail posture. Learning these subtle signals allows owners to de-escalate anxiety before a dog reaches their stress threshold.",
      corePsychologyInsight = "Turid Rugaas identified over 30 'calming signals' dogs use to self-soothe and defuse tension. A yawn or lip lick out of context is almost never fatigue or hunger—it is an early appeasement signal indicating rising stress levels.",
      sections = listOf(
        ArticleSection(
          heading = "1. Calming Signals vs. Physiological Needs",
          body = "When a dog yawns during a training session, turns their head away when an unfamiliar person approaches, or rapidly licks their lips without food present, they are signaling discomfort. Recognizing these low-level stress cues lets you provide space before the dog feels forced to growl or freeze.",
          takeaway = "Never scold a dog for turning away or yawning; acknowledge their request for breathing room.",
        ),
        ArticleSection(
          heading = "2. The Myth of the 'Happy Wagging Tail'",
          body = "A wagging tail simply signifies high physiological arousal—not necessarily happiness. Tail height matters: a high, stiff, fast vibrating wag indicates tension or defensive readiness, while a low, sweeping wag accompanied by soft hips signifies genuine friendly relaxation.",
          takeaway = "Assess the whole body: stiffness and stillness indicate danger even if the tail tip is vibrating."
        ),
        ArticleSection(
          heading = "3. Whale Eye & The Stiffening Pause",
          body = "Whale eye (showing the sclera or white portion of the eye in a crescent shape) combined with a rigid freeze is the penultimate warning sign that a dog feels cornered or overwhelmed. Always immediately increase distance from whatever triggered this posture.",
          takeaway = "A dog that goes completely still is in freeze response; stop your approach immediately."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "What does an out-of-context yawn during a training session usually indicate?",
          options = listOf(
            "The dog didn't sleep well last night",
            "The dog is bored with the training treats",
            "A calming signal indicating rising cognitive fatigue or subtle stress",
            "A sign that the dog is about to bite"
          ),
          correctOptionIndex = 2,
          explanation = "Yawning out of context is a classic calming signal dogs employ when experiencing emotional conflict, confusion, or mild anxiety."
        ),
        QuizQuestion(
          question = "Does every tail wag mean the dog is friendly?",
          options = listOf(
            "Yes, dogs only wag their tails when happy",
            "No, tail wagging merely measures arousal level; a stiff, high wag can signal defensive tension",
            "No, only cats wag their tails when angry",
            "Yes, unless they are simultaneously barking"
          ),
          correctOptionIndex = 1,
          explanation = "Tail wagging indicates emotional arousal. The posture (stiff vs loose), height, and speed determine whether the dog is relaxed, fearful, or aroused."
        )
      )
    ),

    Article(
      id = "art_feline_cognition",
      title = "Feline Territorial Security: The Architecture of Cat Confidence",
      subtitle = "Why vertical space, scent highways, and slow-blinking create an anxiety-free domestic cat.",
      category = ArticleCategory.COGNITION,
      speciesTarget = "Cat",
      readTimeMinutes = 4,
      authorName = "Dr. Marcus Thorne, DVM",
      authorCredentials = "Feline Behavioral Medicine Specialist",
      heroImageResName = "img_cat_behavior",
      summary = "Cats view their world in three-dimensional territory grids and scent signatures. Insecurity arises when scent continuity is disrupted or vertical retreat avenues are blocked.",
      corePsychologyInsight = "Feline facial pheromones (F3 fraction) deposited via cheek-rubbing (allorubbing) establish 'safe zones'. Slow-blinking is a direct ocular expression of trust and vulnerability.",
      sections = listOf(
        ArticleSection(
          heading = "1. The 3D World: Vertical Territory",
          body = "Unlike dogs who navigate primarily horizontally, cats establish hierarchy and security through vertical territory. Wall shelves, cat trees, and accessible high perches prevent floor-level resource conflicts in multi-pet homes and allow timid cats to observe safely without feeling trapped.",
          takeaway = "Provide at least two distinct high vantage points per active room in your home."
        ),
        ArticleSection(
          heading = "2. Scent Commuting & Scratching Psychology",
          body = "Scratching is not spiteful destruction; it is a vital dual scent-and-visual beacon. Cats have interdigital scent glands in their paw pads. Scratching posts must be tall enough for a full vertical stretch and placed along high-traffic scent corridors, not hidden in basements.",
          takeaway = "Position sturdy scratching posts near doorways and sleeping zones where cats naturally wake and claim territory."
        ),
        ArticleSection(
          heading = "3. The Slow-Blink Protocol",
          body = "In the wild, prolonged unbroken eye contact is an aggressive challenge. When a cat looks at you with relaxed eyelids and performs a slow blink, they are signaling safety. Returning the slow blink with softened facial muscles reinforces the inter-species bond.",
          takeaway = "Practice slow-blinking with soft eyes whenever interacting with a nervous or newly adopted cat."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "Why do cats cheek-rub against furniture and your legs?",
          options = listOf(
            "To remove annoying loose fur from their whiskers",
            "To deposit calming facial pheromones and establish familiar scent security",
            "To show dominance over the human owner",
            "Because their gums are itchy"
          ),
          correctOptionIndex = 1,
          explanation = "Cats deposit F3 facial pheromones during allorubbing, marking the environment and bonded companions as safe and familiar."
        )
      )
    ),

    Article(
      id = "art_neurobiology_fear",
      title = "Trigger Stacking & The Neurobiology of Fear in Pets",
      subtitle = "Understanding the amygdala hijack, cortisol half-life, and how minor stressors accumulate into explosive reactions.",
      category = ArticleCategory.PSYCHOLOGY,
      speciesTarget = "All Pets",
      readTimeMinutes = 6,
      authorName = "Sarah Jenkins, MSc, CPDT-KA",
      authorCredentials = "Director of Animal Cognition & Behavior Labs",
      heroImageResName = "img_dog_training",
      summary = "A pet does not suddenly snap out of nowhere. 'Trigger stacking' occurs when multiple minor stress events compound over hours or days, flooding the nervous system with cortisol and epinephrine until a seemingly trivial stimulus crosses their threshold.",
      corePsychologyInsight = "Cortisol from a single frightening event (like a thunderstorm or startled vet visit) can take 48 to 72 hours to return to baseline levels. During this refractory period, the pet is in a state of hypervigilance.",
      sections = listOf(
        ArticleSection(
          heading = "1. How Trigger Stacking Happens",
          body = "Imagine a morning where the doorbell rings (Stressor 1: +20%), a vacuum cleaner turns on (Stressor 2: +30%), and a strange dog barks behind a fence during a walk (Stressor 3: +30%). When a toddler later walks by the dog's food bowl (Stressor 4: +25%), the total surpasses 100%, causing a sudden growl. The owner thinks it was the child, but it was the cumulative stack.",
          takeaway = "Track stressful events across the entire day, not just the moment an undesirable behavior occurred."
        ),
        ArticleSection(
          heading = "2. The Sympathetic Hijack",
          body = "When fear triggers the sympathetic nervous system, blood redirects from the digestive and cognitive frontal cortex to large muscle groups for Fight, Flight, Freeze, or Fiddle About (the 4 Fs). Attempting to teach or punish during an active hijack is biologically ineffective because rational processing is offline.",
          takeaway = "Never try to train or lecture a panicked pet. Move them immediately to a calm sanctuary and wait for adrenaline to subside."
        ),
        ArticleSection(
          heading = "3. Cortisol Decompression Days",
          body = "Following an intense fear event, implement 48 hours of 'decompression': quiet environment, scent-based foraging games, low-demand routines, and avoiding high-stimulus outdoor environments to allow endocrine recovery.",
          takeaway = "Schedule rest and low-arousal enrichment days after high-stress outings."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "How long can cortisol levels remain elevated after a major acute stress event in pets?",
          options = listOf(
            "About 5 to 10 minutes",
            "1 to 2 hours",
            "24 to 72 hours (up to 3 days)",
            "Only until they eat a meal"
          ),
          correctOptionIndex = 2,
          explanation = "Stress hormones like cortisol have a long biological half-life and can keep an animal in a sensitized, hyper-reactive state for 24 to 72 hours."
        )
      )
    ),

    Article(
      id = "art_separation_anxiety",
      title = "Separation Distress vs. Boredom: Diagnosing & Solving Alone-Time Panic",
      subtitle = "How to differentiate between under-stimulated mischief and clinical attachment panic.",
      category = ArticleCategory.BEHAVIOR_SOLUTIONS,
      speciesTarget = "All Pets",
      readTimeMinutes = 5,
      authorName = "Dr. Elena Rostova, PhD",
      authorCredentials = "Certified Applied Animal Behaviorist (CAAB)",
      heroImageResName = "img_hero_pet_mentality",
      summary = "True separation anxiety is an involuntary panic disorder, akin to a human claustrophobia attack. Distinguishing it from simple boredom is crucial for effective treatment.",
      corePsychologyInsight = "Bored pets chew items they enjoy (shoes, remote controls) and sleep when tired. Panicked pets focus destruction on exit points (door frames, window sills), refuse high-value food while alone, and pace incessantly with dilated pupils.",
      sections = listOf(
        ArticleSection(
          heading = "1. The Diagnostic Checklist",
          body = "To diagnose separation anxiety: Set up a video camera. Does pacing, whining, or door scratching begin within the first 10-15 minutes of departure? Does your dog refuse peanut butter or fresh chicken left behind? If yes, it is emotional panic, not bad behavior.",
          takeaway = "If high-value treats remain untouched until you step back through the door, your pet is in fear distress."
        ),
        ArticleSection(
          heading = "2. Desensitizing Departure Cues",
          body = "Pets predict departures via subtle environmental signals: grabbing car keys, putting on a specific jacket, or jiggling door knobs. Practice these cues randomly throughout the day without actually leaving (pick up keys, sit on couch; put on coat, make coffee). This breaks the association between the cue and abandonment.",
          takeaway = "Neutralize departure triggers so keys and shoes lose their predictive panic power."
        ),
        ArticleSection(
          heading = "3. Sub-Threshold Absences",
          body = "The golden rule of separation anxiety rehabilitation is to never let the pet panic. Start with absences of just 5 seconds, returning while they are still calm. Gradually build duration in small increments over weeks.",
          takeaway = "Slow and sub-threshold is the only scientifically proven route to genuine lone-time independence."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "What is a hallmark symptom of clinical separation anxiety vs boredom?",
          options = listOf(
            "The pet eats the entire bowl of treats immediately",
            "The pet destroys toys placed in their dog bed",
            "Destruction focused on exit barriers (doors/windows) and complete refusal of high-value food",
            "Sleeping peacefully on the couch after 5 minutes"
          ),
          correctOptionIndex = 2,
          explanation = "In clinical separation distress, the fight-or-flight panic suppresses appetite and directs escape attempts towards exit barriers."
        )
      )
    ),

    Article(
      id = "art_reinforcement_mechanics",
      title = "The Mechanics of Positive Reinforcement: Bridging Stimulus & Marker Timing",
      subtitle = "Why microsecond marker precision transforms training speed and pet motivation.",
      category = ArticleCategory.COGNITION,
      speciesTarget = "All Pets",
      readTimeMinutes = 4,
      authorName = "Sarah Jenkins, MSc, CPDT-KA",
      authorCredentials = "Director of Animal Cognition & Behavior Labs",
      heroImageResName = "img_dog_training",
      summary = "Animals learn through contingency and contiguity. A marker signal (clicker or verbal 'Yes!') acts as a photographic bridge between the precise desired behavior and the ensuing primary reward.",
      corePsychologyInsight = "B.F. Skinner demonstrated that the primary reward (food/praise) does not need to arrive instantaneously if a conditioned secondary reinforcer (the clicker) marks the exact millisecond of behavioral execution.",
      sections = listOf(
        ArticleSection(
          heading = "1. The Click is a Snapshot",
          body = "If you want to teach a dog to touch their nose to your hand, clicking when their nose makes contact captures that exact snapshot. If you click two seconds later as they turn away, you are accidentally reinforcing turning away.",
          takeaway = "Mark during the exact peak of the desired behavior, then deliver the treat smoothly."
        ),
        ArticleSection(
          heading = "2. Charging the Marker",
          body = "Before teaching commands, teach the meaning of the marker. Click -> treat 15-20 times in a quiet room with zero requirements. The pet quickly learns: 'Click always predicts food!'",
          takeaway = "A well-charged clicker becomes dopamine in auditory form."
        ),
        ArticleSection(
          heading = "3. Variable Schedules of Reinforcement",
          body = "Once a behavior is reliably on cue (90%+ success), shift from continuous reinforcement (treat every time) to variable reinforcement (like a slot machine). This makes the behavior resistant to extinction.",
          takeaway = "Build reliability with 100% treats, then strengthen permanence with intermittent surprises."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "What is the primary role of a training clicker?",
          options = listOf(
            "To distract the pet from barking",
            "To punish mistakes by making a loud startle sound",
            "To act as a precise bridge marker that captures the exact millisecond of desired behavior",
            "To call the pet from long distances"
          ),
          correctOptionIndex = 2,
          explanation = "A clicker serves as a conditioned secondary reinforcer that marks the exact instant of success, giving the animal crystal-clear feedback."
        )
      )
    ),

    Article(
      id = "art_resource_guarding",
      title = "Resource Guarding: From Threat Posture to Confident Sharing",
      subtitle = "Transforming defensive possessiveness into cooperative trade games using counter-conditioning.",
      category = ArticleCategory.BEHAVIOR_SOLUTIONS,
      speciesTarget = "Dog",
      readTimeMinutes = 5,
      authorName = "Dr. Elena Rostova, PhD",
      authorCredentials = "Certified Applied Animal Behaviorist (CAAB)",
      heroImageResName = "img_hero_pet_mentality",
      summary = "Resource guarding is a deeply natural evolutionary survival instinct. Forcibly snatching guarded items confirms the pet's fear that humans are resource thieves, worsening the aggression.",
      corePsychologyInsight = "Classical counter-conditioning changes the pet's emotional response from 'Approaching human = I am going to lose my bone' to 'Approaching human = I get something even better!'",
      sections = listOf(
        ArticleSection(
          heading = "1. The Mistake of 'Dominance' Confiscation",
          body = "Outdated advice suggested putting your hands in a dog's food bowl or taking bones away to show 'who is boss'. In reality, this directly teaches the dog that your hands are a threat to their survival assets, accelerating mild stiffening into biting.",
          takeaway = "Never steal items from your pet's mouth unless it is an immediate toxic emergency."
        ),
        ArticleSection(
          heading = "2. The 'Trading Up' Protocol",
          body = "If your dog has a chew toy, walk past from a safe distance and toss a high-value piece of roast chicken or cheese right next to them, then continue walking away. The dog learns your approach means addition, not subtraction.",
          takeaway = "Always offer a higher-value exchange when taking any object."
        ),
        ArticleSection(
          heading = "3. Safe Feeding Zones",
          body = "Give pets a dedicated, undisturbed feeding corner where nobody enters or reaches near them while they eat. Absolute peace of mind at mealtimes prevents guard anxiety from forming.",
          takeaway = "Respect eating boundaries to build a foundation of secure trust."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "If your pet growls when you approach their chew toy, what is the best scientific response?",
          options = listOf(
            "Yell and pry the toy out of their mouth immediately to show dominance",
            "Toss a higher-value treat nearby from a safe distance and increase space without forcing a confrontation",
            "Tap their nose with a rolled newspaper",
            "Ignore them for 24 hours"
          ),
          correctOptionIndex = 1,
          explanation = "Forcibly taking items escalates guarding into defensive biting. Tossing a superior treat from distance changes the emotional state to positive anticipation."
        )
      )
    ),

    Article(
      id = "art_mental_enrichment",
      title = "Brain Games & Olfactory Stimulation: The 20-Minute Sniff Walk",
      subtitle = "Why engaging your pet's sense of smell tires them out faster and healthier than physical running.",
      category = ArticleCategory.ENRICHMENT,
      speciesTarget = "All Pets",
      readTimeMinutes = 4,
      authorName = "Sarah Jenkins, MSc, CPDT-KA",
      authorCredentials = "Director of Animal Cognition & Behavior Labs",
      heroImageResName = "img_cat_behavior",
      summary = "A dog's olfactory cortex is roughly 40 times larger than a human's, capable of processing up to 300 million scent receptors. Scent work drastically lowers heart rate and releases natural calming endorphins.",
      corePsychologyInsight = "Studies in canine biometrics show that 15 minutes of dedicated sniffing work reduces cortisol and blood pressure significantly more than a 45-minute structured heel march.",
      sections = listOf(
        ArticleSection(
          heading = "1. The 'Decompression Sniffari'",
          body = "Instead of forcing your dog to walk in a rigid heel, use a 10-foot or 15-foot long line and let the dog choose the direction and sniff every blade of grass at their own leisure. Scent processing is the canine equivalent of reading a rich newspaper.",
          takeaway = "Dedicate at least half of weekly walks to slow, unstructured sniffing sessions."
        ),
        ArticleSection(
          heading = "2. DIY Foraging & Snuffle Mats",
          body = "Ditch the traditional food bowl. Scatter dry kibble in a crumpled towel, snuffle mat, or cardboard puzzle box. Forcing animals to forage satisfies innate predatory search instincts and prevents hyperactive boredom.",
          takeaway = "Make meals an engaging brain puzzle rather than a 30-second bowl gulp."
        ),
        ArticleSection(
          heading = "3. Nosework Scent Games for Cats & Dogs",
          body = "Hide treats under upside-down muffin tins or behind furniture corners. Teach the cue 'Find It!'. Watching their focus sharpen is one of the most rewarding enrichment activities for any pet owner.",
          takeaway = "Active scent puzzles burn mental energy without joint wear on senior pets."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "How does dedicated olfactory sniffing affect a pet's physiological state?",
          options = listOf(
            "It spikes their heart rate into fight-or-flight mode",
            "It provides deep cognitive stimulation, lowers pulse, and releases soothing endorphins",
            "It makes them aggressively territorial",
            "It has zero cognitive effect compared to running"
          ),
          correctOptionIndex = 1,
          explanation = "Olfactory processing engages massive regions of the pet's brain, promoting parasympathetic calming and fulfilling natural foraging instincts."
        )
      )
    ),

    Article(
      id = "art_leash_reactivity",
      title = "Leash Reactivity Decoded: Frustrated Greeter vs. Fear Aggression",
      subtitle = "Breaking the cycle of barking and lunging on leash using the Look-At-That (LAT) protocol.",
      category = ArticleCategory.BEHAVIOR_SOLUTIONS,
      speciesTarget = "Dog",
      readTimeMinutes = 6,
      authorName = "Dr. Marcus Thorne, DVM",
      authorCredentials = "Feline & Canine Behavioral Medicine Specialist",
      heroImageResName = "img_dog_training",
      summary = "When a dog barks or lunges at other dogs on leash, it is almost always caused by leash barrier frustration or fear of confinement, not malicious spite.",
      corePsychologyInsight = "A leash removes a dog's primary evolutionary defense: flight. When a restrained dog feels unable to create distance, their nervous system defaults to explosive defensive displays to force the other dog away.",
      sections = listOf(
        ArticleSection(
          heading = "1. Finding the Critical Threshold Distance",
          body = "Every reactive pet has a 'threshold distance'—the distance at which they can notice the trigger without barking or stiffening. If your dog reacts at 20 feet, work at 40 feet where their thinking brain remains active.",
          takeaway = "Always work beneath your pet's reaction threshold; never push them into a full panic meltdown."
        ),
        ArticleSection(
          heading = "2. The Look At That (LAT) Game",
          body = "When your dog spots another dog at a safe distance: the instant they look at the trigger, mark with your clicker or verbal 'Yes!' and reward with high-value chicken. Repeat until spotting a dog automatically prompts them to whip their head around to you in happy expectation.",
          takeaway = "Turn the sight of other dogs into a conditioned cue to look at you for treats."
        ),
        ArticleSection(
          heading = "3. Emergency U-Turns with Joy",
          body = "Teach a cheerful 'Let's Go!' cue at home with zero distractions, practicing 180-degree pivot turns. When surprised by a trigger around a street corner, you can execute a smooth U-turn without tension.",
          takeaway = "Master the emergency exit maneuver before you need it in high-stress situations."
        )
      ),
      quiz = listOf(
        QuizQuestion(
          question = "What is the 'threshold distance' in reactive training?",
          options = listOf(
            "The length of the leash in feet",
            "The distance at which a pet can perceive a trigger while remaining calm and capable of learning",
            "The maximum distance a dog can run in one sprint",
            "The point at which a pet bites"
          ),
          correctOptionIndex = 1,
          explanation = "Operating beneath the threshold distance ensures the pet's thinking brain stays engaged without triggering sympathetic fight-or-flight panic."
        )
      )
    )
  )

  val dailyPetFacts: List<Pair<String, String>> = listOf(
    "Canine Nose Power" to "A dog's nose has a special structure that allows them to breathe in and out simultaneously, creating an uninterrupted stream of scent analysis!",
    "Feline Whisker Radar" to "Cat whiskers are deeply rooted in nerve endings called proprioceptors that can detect subtle air current shifts, helping them gauge spatial gaps in pitch darkness.",
    "The Yawn De-escalator" to "If a dog feels nervous during training, slowly yawning and blinking at them sends a universal canine calming signal that you mean zero harm.",
    "Dopamine in Training" to "Dopamine peaks not when the pet eats the treat, but at the exact sound of the clicker predicting the reward! The anticipation itself is the joy.",
    "Cat Purr Healing" to "A domestic cat's purr vibrates at a precise frequency of 20 to 140 Hz, which has been shown in veterinary studies to promote bone density and tissue repair."
  )
}
