package com.example.data

import com.example.model.BehaviorSignal
import com.example.model.EmotionalState

object BehaviorDecoderData {
  val signals: List<BehaviorSignal> = listOf(
    // DOG SIGNALS
    BehaviorSignal(
      id = "dog_whale_eye",
      species = "Dog",
      bodyPart = "Eyes",
      observationTitle = "Whale Eye (Sclera Visible)",
      description = "Dog shows the white crescent border of their eye while holding their head slightly turned away with a stiff neck.",
      emotionalState = EmotionalState.DEFENSIVE_FEARFUL,
      whatItMeans = "The dog feels trapped, cornered, or heavily pressured and is closely tracking movement to protect itself.",
      scientificExplanation = "The ocular muscles tense as peripheral scanning overrides relaxed foveal focus during an active amygdala fear response.",
      whatOwnerShouldDo = listOf(
        "Immediately increase distance between the dog and whatever triggered the look.",
        "Turn your own body sideways and avoid direct eye contact.",
        "Drop a treat gently on the floor and give the dog an easy escape route.",
      ),
      whatNOTToDo = listOf(
        "Do NOT lean over the dog or reach out to pet their head.",
        "Do NOT scold or force the dog to look at you.",
        "Do NOT corner the dog into a hug."
      )
    ),

    BehaviorSignal(
      id = "dog_play_bow",
      species = "Dog",
      bodyPart = "Body Posture",
      observationTitle = "Play Bow (Front Down, Rear Up)",
      description = "Front elbows touching the ground with hindquarters high in the air, loose wagging tail, relaxed open mouth.",
      emotionalState = EmotionalState.PLAYFUL_EXCITED,
      whatItMeans = "A universal canine meta-communication that says: 'Everything I do next is just a game, not serious aggression!'",
      scientificExplanation = "Marc Bekoff documented play bows as critical communicative punctuation markers preventing misunderstandings during rough-and-tumble mock combat.",
      whatOwnerShouldDo = listOf(
        "Engage in playful interactive games (tug-of-war, fetch, gentle chase).",
        "Reward the enthusiasm with high-energy verbal praise."
      ),
      whatNOTToDo = listOf(
        "Do NOT mistake the intense energy or play growls for real aggression."
      )
    ),

    BehaviorSignal(
      id = "dog_stiff_high_tail",
      species = "Dog",
      bodyPart = "Tail",
      observationTitle = "High Stiff Flagging Tail",
      description = "Tail held erect over the spine, vibrating rapidly with small tight arcs; weight shifted onto front paws.",
      emotionalState = EmotionalState.ALERT_FOCUSED,
      whatItMeans = "High physiological arousal, vigilance, and readiness to act defensively or assertively if challenged.",
      scientificExplanation = "High tail posture maximizes scent dissemination from anal glands while telegraphing physical height to rivals.",
      whatOwnerShouldDo = listOf(
        "Calmly call the dog away using a cheerful 'Let's Go!' or 'Touch' command.",
        "Add physical distance from approaching stimuli.",
        "Assess whether the environment is pushing the dog over their emotional threshold."
      ),
      whatNOTToDo = listOf(
        "Do NOT assume the wagging means the dog wants to be petted by strangers.",
        "Do NOT jerk harshly on the leash as tension transmits down the line."
      )
    ),

    BehaviorSignal(
      id = "dog_lip_licking_yawning",
      species = "Dog",
      bodyPart = "Mouth/Face",
      observationTitle = "Rapid Lip Licking & Out-of-Context Yawn",
      description = "Quick tongue flick over the nose or a deep wide yawn during training or when someone approaches closely.",
      emotionalState = EmotionalState.ANXIOUS_STRESSED,
      whatItMeans = "Low-level conflict, anxiety, or confusion; the dog is actively trying to self-soothe and pacify the human.",
      scientificExplanation = "A classic Rugaas calming signal. Yawning increases brain oxygenation and stimulates vagal tone to downregulate stress.",
      whatOwnerShouldDo = listOf(
        "Lower the difficulty of your current training step.",
        "Take a 2-minute break and let the dog sniff or drink water.",
        "Give the dog more personal space."
      ),
      whatNOTToDo = listOf(
        "Do NOT assume the dog is just sleepy or tired.",
        "Do NOT repeat frustrated commands louder."
      )
    ),

    BehaviorSignal(
      id = "dog_zoomies_frenetic",
      species = "Dog",
      bodyPart = "Body Posture",
      observationTitle = "FRAPs (Zoomies / Frenetic Activity)",
      description = "Sprinting in rapid erratic circles with tucked hindquarters, wild eyes, sudden sudden slides into grass.",
      emotionalState = EmotionalState.PLAYFUL_EXCITED,
      whatItMeans = "Frenetic Random Activity Periods (FRAPs)—a healthy burst of pure joy and physical tension release, often after a bath or crate session.",
      scientificExplanation = "A release valve for accumulated adrenaline and dopamine, allowing the autonomic nervous system to burn off excess motor drive.",
      whatOwnerShouldDo = listOf(
        "Ensure the area is safe (clear away sharp furniture or open gates).",
        "Let the pet enjoy the 60-second burst naturally."
      ),
      whatNOTToDo = listOf(
        "Do NOT chase or tackle the pet; let the loop complete safely."
      )
    ),

    // CAT SIGNALS
    BehaviorSignal(
      id = "cat_slow_blink",
      species = "Cat",
      bodyPart = "Eyes",
      observationTitle = "Slow Blink & Soft Gaze",
      description = "Cat looks directly at human with softened, heavy eyelids and deliberately closes their eyes slowly before opening them gently.",
      emotionalState = EmotionalState.CALM_CONTENT,
      whatItMeans = "The ultimate feline sign of safety, trust, and affection. The cat feels completely secure in your presence.",
      scientificExplanation = "In predator-prey dynamics, breaking visual surveillance in the presence of another creature is an act of total vulnerability and trust.",
      whatOwnerShouldDo = listOf(
        "Slowly blink back at the cat with a gentle, relaxed expression.",
        "Speak softly or offer an outstretched finger for a voluntary cheek sniff."
      ),
      whatNOTToDo = listOf(
        "Do NOT stare back with wide, unblinking eyes (this is perceived as a predatory threat)."
      )
    ),

    BehaviorSignal(
      id = "cat_twitching_tail_tip",
      species = "Cat",
      bodyPart = "Tail",
      observationTitle = "Thumping or Swishing Tail",
      description = "Tail lashing from side to side in wide heavy thumps against the floor; ears rotating back like airplane wings.",
      emotionalState = EmotionalState.OVERSTIMULATED,
      whatItMeans = "Sensory overload, rising irritation, or predatory focus. Petting tolerance has expired.",
      scientificExplanation = "Petting-induced aggression occurs when repetitive tactile stimulation overwhelms cutaneous nerve endings.",
      whatOwnerShouldDo = listOf(
        "Immediately remove your hands and stop petting.",
        "Give the cat space to walk away or reset.",
        "Redirect predatory focus to a wand toy."
      ),
      whatNOTToDo = listOf(
        "Do NOT keep petting just because the cat hasn't hissed yet.",
        "Do NOT pick up or constrain a cat with a thumping tail."
      )
    ),

    BehaviorSignal(
      id = "cat_airplane_ears",
      species = "Cat",
      bodyPart = "Ears",
      observationTitle = "Airplane Ears (Flattened Outward/Back)",
      description = "Ears pinned back flat against the skull or rotated horizontal to the sides; head lowered.",
      emotionalState = EmotionalState.DEFENSIVE_FEARFUL,
      whatItMeans = "The cat is frightened, defensive, and protecting its ears from potential combat damage.",
      scientificExplanation = "Feline ear pinnae contain 32 individual muscles. Flattening protects the delicate hearing apparatus from claw strikes in defensive postures.",
      whatOwnerShouldDo = listOf(
        "Give the cat an immediate clear pathway to a high perch or dark safe cave.",
        "Dim bright lights and eliminate loud background noises."
      ),
      whatNOTToDo = listOf(
        "Do NOT reach over their head or attempt to corner them.",
        "Do NOT try to cuddle or hold the cat."
      )
    ),

    BehaviorSignal(
      id = "cat_upright_quiver_tail",
      species = "Cat",
      bodyPart = "Tail",
      observationTitle = "Upright Tail with a Slight Tip Curve",
      description = "Tail held vertically like a flagpole with a subtle relaxed question-mark hook at the very tip, vibrating with gentle joy.",
      emotionalState = EmotionalState.CALM_CONTENT,
      whatItMeans = "A warm, cheerful greeting! The cat is happy to see you and welcomes social interaction.",
      scientificExplanation = "An evolutionary greeting posture derived from kittens greeting their mothers, signaling benign social intentions.",
      whatOwnerShouldDo = listOf(
        "Offer your knuckle or finger at nose height for a friendly scent exchange.",
        "Provide gentle scratches around the cheeks and base of the chin."
      ),
      whatNOTToDo = listOf(
        "Do NOT pet the belly unless you know your cat specifically enjoys it (the belly is a vulnerable zone)."
      )
    )
  )
}
