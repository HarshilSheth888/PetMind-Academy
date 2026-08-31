package com.example.data

import com.example.model.GuideDifficulty
import com.example.model.TrainingGuide
import com.example.model.TrainingStep

object TrainingGuidesData {
  val guides: List<TrainingGuide> = listOf(
    TrainingGuide(
      id = "guide_loose_leash",
      title = "Loose Leash Walking: The Silky Leash Protocol",
      shortDescription = "Transform pulling and frantic lunging into relaxed, shoulder-aligned heel walking using tension-release mechanics.",
      difficulty = GuideDifficulty.FOUNDATION,
      speciesTarget = "Dog",
      category = "Manners",
      estimatedSessions = "10 - 14 sessions (5 min/day)",
      gearNeeded = listOf("Standard 6-ft leash (non-retractable)", "Front-clip Y-harness", "High-value soft treats (pea-sized)", "Clicker"),
      whyItWorksPsychologically = "Dogs have an innate 'opposition reflex' (thigmotaxis). When they feel pressure against their chest/neck, their instinct is to push harder into the resistance. Teaching tension-as-a-cue flips this instinct so gentle leash pressure means 'check in with owner'.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "Indoors Zero-Distraction Check-In",
          instruction = "Attach the leash in a quiet living room. Stand still. When your dog takes one step towards you or looks at your eyes, immediately CLICK and feed a treat at your hip line.",
          cueWord = "Focus / Yes",
          petMindset = "Standing near my human's leg delivers continuous rewards; pulling away delivers nothing.",
          successCriteria = "8 out of 10 voluntary check-ins within 2 minutes of standing still.",
          commonMistake = "Feeding the treat in front of you instead of directly at your hip seam."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "The 'Be a Tree' Freeze",
          instruction = "Take 3 steps forward. The millisecond the leash goes taut, freeze like a tree. Do not jerk the leash. Wait silently. The moment your dog turns back to look at you, CLICK and treat.",
          cueWord = null,
          petMindset = "Forward movement only happens when the leash has a happy U-shaped curve.",
          successCriteria = "Dog immediately stops pulling and slackens leash within 2 seconds of human stopping.",
          commonMistake = "Pulling back against the dog instead of becoming a motionless anchor."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "The 180-Degree About-Turn",
          instruction = "Walk forward. Without tension, say 'Let's Go!' in a cheerful tone, pivot 180 degrees, and walk the other way. When your dog catches up to your stride, CLICK and reward.",
          cueWord = "Let's Go!",
          petMindset = "My human changes direction unpredictably! I need to keep one eye on them to stay in the game.",
          successCriteria = "Dog pivots and follows immediately upon hearing the cue across 5 consecutive turns.",
          commonMistake = "Using a frustrated or scolding voice instead of an exciting invitation."
        ),
        TrainingStep(
          stepNumber = 4,
          title = "Graduated Outdoor Environment",
          instruction = "Move to your driveway or quiet sidewalk. Practice 5-minute sessions. If your dog pulls toward a smell, freeze, wait for eye contact, then release them with 'Go Sniff!' to use the scent as the reward.",
          cueWord = "Go Sniff!",
          petMindset = "Checking in with my human is the magic key that unlocks sniffing privileges!",
          successCriteria = "Walking 100 meters on a slack leash with fewer than 2 stops for pulling.",
          commonMistake = "Extending the session too long until the dog becomes overstimulated."
        )
      ),
      troubleshootingTips = listOf(
        "If your dog ignores treats outside, you are working in an environment with too many competing distractions. Step back to your doorway or backyard.",
        "Ensure treats are soft and smelly (like cut-up string cheese or liver) so your dog can swallow quickly without breaking stride."
      )
    ),

    TrainingGuide(
      id = "guide_emergency_recall",
      title = "Emergency Recall: The 100% Rocket Come",
      shortDescription = "Build an unbreakable recall cue that makes your pet sprint to you away from squirrels, open gates, or other dogs.",
      difficulty = GuideDifficulty.INTERMEDIATE,
      speciesTarget = "Dog & Cat",
      category = "Safety",
      estimatedSessions = "12 - 16 sessions",
      gearNeeded = listOf("High-value Jackpot reward (sardines, real meat)", "Clicker or Whistle", "15-ft Long training line"),
      whyItWorksPsychologically = "A recall must never be associated with end-of-fun (e.g. going inside, bath time, nail clipping). By creating a high-dopamine jackpot party every time the emergency cue is sounded, the response becomes an involuntary positive reflex.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "Charging the Emergency Cue",
          instruction = "Choose a distinct word you never use casually (e.g. 'ROCKET!' or 'HERE NOW!'). Stand 3 feet away. Shout the cue enthusiastically, CLICK immediately, and deliver a 15-second jackpot of continuous tiny treat bites.",
          cueWord = "ROCKET!",
          petMindset = "That word means an avalanche of the best food on Earth is happening right now!",
          successCriteria = "Pet's head snaps toward you with wide eyes within 0.5 seconds of hearing the cue.",
          commonMistake = "Using the standard 'come' command that has already been poisoned by previous casual overuse."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "The Indoor Chase Game",
          instruction = "Wait until your pet is in another room sniffing something mild. Shout the cue, turn, and run backwards a few steps. When they catch you, celebrate with an enthusiastic jackpot reward.",
          cueWord = "ROCKET!",
          petMindset = "Running after my human is the most thrilling game ever!",
          successCriteria = "Pet sprints from another room at full speed 5 times in a row.",
          commonMistake = "Advancing toward the pet instead of running away from them to trigger chase drive."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "Long-Line Recall with Controlled Distractions",
          instruction = "In an enclosed yard or park with a 15-foot line trailing loosely on the grass. Wait until your pet is moderately distracted. Call the cue, reel the line gently if needed, and reward with high value meat.",
          cueWord = "ROCKET!",
          petMindset = "Even when exciting smells are around, that cue is 100x more rewarding!",
          successCriteria = "Instant recall from 15 feet away across 4 different outdoor sessions.",
          commonMistake = "Calling the pet to you to give them medication or clip their nails immediately afterward."
        )
      ),
      troubleshootingTips = listOf(
        "Never use your emergency recall cue unless you have high-value jackpot treats in your hand or pocket.",
        "After calling your pet to you during play, reward them and immediately say 'Go Play!' so they don't think coming to you ends their freedom."
      )
    ),

    TrainingGuide(
      id = "guide_impulse_control_zen",
      title = "Impulse Control: The 'Leave It' Zen Game",
      shortDescription = "Teach your pet that the fastest way to get what they want is to voluntarily disengage from temptation.",
      difficulty = GuideDifficulty.FOUNDATION,
      speciesTarget = "Dog & Cat",
      category = "Manners",
      estimatedSessions = "6 - 8 sessions",
      gearNeeded = listOf("Medium-value treats (in closed hand)", "High-value jackpot treats (in opposite pocket)", "Clicker"),
      whyItWorksPsychologically = "Operant conditioning: We do not yank the item away; the pet learns that nudging, pawing, or barking at the item causes it to disappear behind a closed fist, while looking away unlocks a superior reward.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "Closed Fist Zen",
          instruction = "Hold a piece of kibble in a tightly closed fist. Present it to your pet. They will sniff and lick your hand. Stay completely quiet and still. The second they back their nose away even 1 inch, CLICK and feed a high-value treat from your OTHER hand.",
          cueWord = null,
          petMindset = "Mugging the hand gets me nothing; backing away makes the reward appear from the other side!",
          successCriteria = "Pet immediately looks away from the closed fist without touching it 5 times consecutively.",
          commonMistake = "Opening your hand while they are still sniffing or pawing."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "Open Palm with Cover Reflex",
          instruction = "Open your hand flat with the treat exposed. If your pet lunges, close your fist immediately (do not scold). When they back up and pause, say 'Leave It', CLICK, and reward with the other hand.",
          cueWord = "Leave It",
          petMindset = "An open hand is a challenge of self-control. I win by maintaining eye contact with my owner.",
          successCriteria = "Pet stays back from an open hand holding bacon for 5 uninterrupted seconds.",
          commonMistake = "Letting the pet eat the treat off the open hand instead of rewarding from your secret stash."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "The Floor Drop Challenge",
          instruction = "Drop a low-value treat on the floor under your foot. Say 'Leave It'. When your pet stops sniffing your shoe and looks up at your eyes, CLICK and feed roast chicken from above.",
          cueWord = "Leave It",
          petMindset = "Floor food is boring; my human has the gourmet feast!",
          successCriteria = "Dropping treats while walking without the pet lunging to scoop them up.",
          commonMistake = "Stepping away before the pet has fully shifted focus to your face."
        )
      ),
      troubleshootingTips = listOf(
        "Always reward with something better than what they were asked to leave.",
        "Practice with toys and non-food items like dropped socks once food impulse control is solid."
      )
    ),

    TrainingGuide(
      id = "guide_crate_comfort",
      title = "Crate & Safe Den Comfort Conditioning",
      shortDescription = "Create a positive sanctuary where your pet chooses to relax voluntarily without barrier distress.",
      difficulty = GuideDifficulty.FOUNDATION,
      speciesTarget = "Universal",
      category = "Confidence",
      estimatedSessions = "8 - 10 sessions",
      gearNeeded = listOf("Properly sized crate / cozy carrier", "Comfortable orthopedic mat", "Stuffed frozen KONG or lick mat"),
      whyItWorksPsychologically = "Canines and felines are natural den animals when given agency. Distress occurs when the crate is used as an isolation punishment or when doors are shut prematurely before the pet builds positive valence.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "The Open Treasure Box",
          instruction = "Prop the crate door permanently open with a bungee cord. Place cozy blankets and high-value surprise treats deep in the back while the pet is in another room. Let them discover it on their own.",
          cueWord = null,
          petMindset = "This cozy cave magically spawns delicious treasures all on its own!",
          successCriteria = "Pet walks into the crate voluntarily with relaxed tail posture to check for goodies.",
          commonMistake = "Pushing or forcing the pet inside physically."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "Mealtime Association & The 'Den' Cue",
          instruction = "Feed all regular daily meals inside the open crate. Pair with the cue word 'Go to Bed' or 'Crate'. Once inside eating, praise calmly.",
          cueWord = "Go to Bed",
          petMindset = "The crate is where the best meals of the day take place.",
          successCriteria = "Pet trots into the crate happily upon hearing the verbal cue.",
          commonMistake = "Closing the door while they are eating during early stages."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "Micro-Door Closure with Frozen Enrichment",
          instruction = "Give your pet a frozen peanut-butter/wet-food KONG in the crate. Close the door for 30 seconds while you sit quietly right beside it reading a book. Open the door before they finish the treat.",
          cueWord = "Rest",
          petMindset = "The door being closed is relaxing because I have this delicious puzzle and my human is right here.",
          successCriteria = "Pet happily licks enrichment puzzle without vocalizing for 10 minutes with door closed.",
          commonMistake = "Letting the pet out only when they are barking or scratching (this accidentally reinforces barking)."
        )
      ),
      troubleshootingTips = listOf(
        "Never use the crate for timeouts or disciplinary punishment.",
        "Cover 3 sides with a breathable dark blanket to create a true calming cave atmosphere."
      )
    ),

    TrainingGuide(
      id = "guide_touch_targeting",
      title = "Hand Targeting: The Nose-Touch Super-Tool",
      shortDescription = "Teach your dog or cat to touch their nose to your palm on cue—the Swiss Army knife of behavioral redirection.",
      difficulty = GuideDifficulty.FOUNDATION,
      speciesTarget = "Universal",
      category = "Focus",
      estimatedSessions = "4 - 6 sessions",
      gearNeeded = listOf("Clicker", "Tasty small treats", "Your open flat hand"),
      whyItWorksPsychologically = "Targeting creates a clear, unambiguous physical target that shifts an animal's cognitive focus from emotional reactivity to a physical game. It allows you to steer your pet like a steering wheel without touching their collar.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "Presenting the Palm Target",
          instruction = "Hold your flat palm 2 inches from your pet's nose. Naturally, they will lean forward to sniff it. The exact microsecond their wet nose touches your skin, CLICK and feed a treat directly into your palm.",
          cueWord = "Touch",
          petMindset = "Booping my nose on that palm makes a click and a treat appear!",
          successCriteria = "Pet boops your palm 9 out of 10 times within 1 second of presentation.",
          commonMistake = "Moving your hand towards their nose instead of letting them make the forward reach."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "Adding Distance and Angles",
          instruction = "Present your hand to the left, to the right, low near the floor, and slightly behind you. Say 'Touch!'. Click and reward upon contact.",
          cueWord = "Touch!",
          petMindset = "I can follow that palm anywhere in the room!",
          successCriteria = "Pet walks 5 feet across the room to boop your hand accurately.",
          commonMistake = "Presenting the hand repeatedly without resetting it behind your back between reps."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "Targeting Past Distractions",
          instruction = "Use 'Touch!' to guide your pet smoothly past a barking dog, off the furniture, into a car crate, or onto a scale at the vet.",
          cueWord = "Touch!",
          petMindset = "I know this game so well, I'd rather do this than worry about the vet room!",
          successCriteria = "Successfully redirecting pet focus away from a moderate distraction using 3 rapid touches.",
          commonMistake = "Failing to reward generously in difficult environments."
        )
      ),
      troubleshootingTips = listOf(
        "If a cat is reluctant to touch your palm, rub a tiny bit of tuna juice or Churu on your finger first.",
        "Keep sessions to 2 minutes of rapid-fire fun touches to keep energy high."
      )
    ),

    TrainingGuide(
      id = "guide_polite_greetings",
      title = "Four-on-the-Floor: Polite Greeting Protocol",
      shortDescription = "Eliminate jumping on arriving guests by teaching an incompatible default behavior: Sitting for attention.",
      difficulty = GuideDifficulty.INTERMEDIATE,
      speciesTarget = "Dog",
      category = "Manners",
      estimatedSessions = "8 - 12 sessions",
      gearNeeded = listOf("Treat pouch", "Clicker", "Helpful human assistant / friend"),
      whyItWorksPsychologically = "Dogs jump because humans have faces up high and jumping is how puppies solicit regurgitated food and lick muzzles. An animal cannot physically jump and sit simultaneously. We make sitting the only behavior that unlocks greeting attention.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "The Cold-Shoulder Fold",
          instruction = "Whenever you enter a room and your dog jumps, immediately cross your arms, turn your back, and look at the ceiling. Zero eye contact, zero talking, zero pushing away. The second all 4 paws touch the ground, turn and calmly say 'Good', then drop a treat.",
          cueWord = null,
          petMindset = "Jumping makes my human turn into an uninteresting statue. Four paws on floor makes them turn back to life.",
          successCriteria = "Dog immediately keeps all 4 paws grounded when owner enters the room.",
          commonMistake = "Pushing the dog off with your hands (to the dog, hand contact is playful wrestling attention!)."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "The Default Greeting Sit",
          instruction = "Approach your dog. Before they have a chance to jump, ask for a 'Sit', CLICK, and shower them with chest scratches and treats down at their eye level.",
          cueWord = "Sit to Say Hi",
          petMindset = "Sitting down like a statue brings human hands and scratches right to me!",
          successCriteria = "Dog automatically sits when any family member approaches without needing to be asked.",
          commonMistake = "Waiting until they are already airborne before trying to say sit."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "Guest at the Door Simulation",
          instruction = "Have a helper knock or ring the doorbell. Keep dog on a light leash or behind a baby gate. Guest only steps forward when the dog sits. If dog breaks sit, guest takes one step back.",
          cueWord = "Say Hello",
          petMindset = "My butt on the floor controls the guest's forward movement!",
          successCriteria = "Calm greeting with an unfamiliar visitor without front paws leaving the floor.",
          commonMistake = "Allowing guests to pet the dog while the dog is whining and jumping."
        )
      ),
      troubleshootingTips = listOf(
        "Instruct all guests beforehand: 'Please ignore the dog completely until all four paws are on the ground.'",
        "Scatter a handful of treats on the ground (Treat Tornado) when guests enter to redirect adrenaline downward."
      )
    ),

    TrainingGuide(
      id = "guide_cooperative_care",
      title = "Cooperative Care: Stress-Free Nail Trims & Handling",
      shortDescription = "Give your pet a consent signal for grooming, ear drops, and paw handling so vet visits become tear-free.",
      difficulty = GuideDifficulty.BEHAVIOR_MOD,
      speciesTarget = "Universal",
      category = "Confidence",
      estimatedSessions = "12 - 18 micro-sessions",
      gearNeeded = listOf("Nail clippers or grinder", "High-value lickable treat (peanut butter/Churu)", "Towel / Non-slip mat"),
      whyItWorksPsychologically = "When an animal feels physically restrained, panic sets in. Cooperative care establishes a 'bucket game' or 'chin rest' consent protocol: As long as the pet holds their chin in your hand, grooming happens. If they lift their head, grooming stops instantly. Control eliminates fear.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "The Chin-Rest Consent Cue",
          instruction = "Teach your pet to rest their chin in your open palm. Click and treat as long as the chin stays down. Practice holding for 5 to 10 seconds.",
          cueWord = "Chin",
          petMindset = "Holding my chin here means treats are flowing. If I ever feel uncomfortable, I just lift my head to pause.",
          successCriteria = "Holding a relaxed chin rest in your palm for 10 solid seconds.",
          commonMistake = "Trapping their head with your thumb instead of maintaining a flat resting cradle."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "Tool Desensitization & Object Touch",
          instruction = "While pet maintains chin rest, touch the nail clippers to their shoulder or leg. Click and reward. Do not clip yet! The tool predicts cheese.",
          cueWord = null,
          petMindset = "The clippers touching my fur is just another fun prop in the treat game.",
          successCriteria = "Zero flinching or head lifting when clippers touch all 4 paws.",
          commonMistake = "Rushing to cut a nail before the pet is 100% relaxed with tool contact."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "One Nail Micro-Clip with Jackpot",
          instruction = "During chin rest, clip just the microscopic tip of ONE single nail. Immediately deliver a massive jackpot treat and end the session on a triumphant high note.",
          cueWord = "Good Job!",
          petMindset = "One little click sound on my claw gave me a whole spoonful of peanut butter!",
          successCriteria = "Doing one paw smoothly across 4 days with zero struggle.",
          commonMistake = "Trying to do all 18 claws in one sitting on a sensitized pet."
        )
      ),
      troubleshootingTips = listOf(
        "Never clip past the quick (pink blood vessel). If using a grinder, use short 1-second taps so heat does not build up.",
        "If the pet pulls their paw away, respect the communication, wait 10 seconds, and offer the chin rest again."
      )
    ),

    TrainingGuide(
      id = "guide_cat_clicker_tricks",
      title = "Feline Agility & Clicker High-Five",
      shortDescription = "Debunk the myth that cats can't be trained while strengthening neural connections and bonding.",
      difficulty = GuideDifficulty.INTERMEDIATE,
      speciesTarget = "Cat",
      category = "Tricks",
      estimatedSessions = "6 - 8 micro-sessions (2 min/session)",
      gearNeeded = listOf("Clicker (or soft pen-click)", "Churu / lickable squeeze treat", "Target stick or pencil"),
      whyItWorksPsychologically = "Cats are obligate carnivores with razor-sharp associative learning capabilities. Clicker training prevents feline indoor depression, reduces obsessive grooming, and channels predatory pounce drive into constructive puzzles.",
      steps = listOf(
        TrainingStep(
          stepNumber = 1,
          title = "The Target Stick Boop",
          instruction = "Hold the tip of a pencil 1 inch from your cat's whiskers. When they stretch out to sniff it, CLICK and offer a 2-second lick of Churu from the tube.",
          cueWord = "Target",
          petMindset = "Touching that stick with my nose is the fastest way to get gourmet puree!",
          successCriteria = "Cat follows target stick across the couch 4 times in a row.",
          commonMistake = "Holding long training sessions; cat sessions should never exceed 2 to 3 minutes."
        ),
        TrainingStep(
          stepNumber = 2,
          title = "Shaping the High Five Paw Tap",
          instruction = "Hold your flat palm slightly above cat eye level. Naturally, the cat will reach up with a front paw to explore or bat at your hand. The instant their paw pad touches your palm, CLICK and reward.",
          cueWord = "High Five",
          petMindset = "A high-five tap on the human hand triggers the snack fountain!",
          successCriteria = "Cat deliberately raises paw and taps your open hand on cue.",
          commonMistake = "Grabbing the cat's paw with your fingers."
        ),
        TrainingStep(
          stepNumber = 3,
          title = "The Over-the-Leg Jump Agility",
          instruction = "Sit on the floor with one leg extended. Use the target stick to guide your cat to step or hop gracefully over your leg. Click mid-air and treat on landing.",
          cueWord = "Hop!",
          petMindset = "I am a majestic panther clearing indoor obstacle courses!",
          successCriteria = "Cat smoothly bounds over your leg when summoned.",
          commonMistake = "Making the jump too high before their confidence is solid."
        )
      ),
      troubleshootingTips = listOf(
        "Train right before regular meal times when your cat is alert and motivated.",
        "Use a quiet clicker or soft tongue click if your cat is sound-sensitive."
      )
    )
  )
}
