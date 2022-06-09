package me.naoti.panelapp.utils

import kotlin.random.Random

val WordBank = listOf<String>(
    "delicate",
    "obsequious",
    "shelf",
    "misty",
    "wave",
    "creator",
    "wish",
    "uptight",
    "sisters",
    "introduce",
    "blow",
    "serious",
    "class",
    "well-made",
    "coal",
    "baby",
    "absent",
    "strong",
    "enter",
    "salt",
    "nonstop",
    "chubby",
    "notice",
    "business",
    "overt",
    "ladybug",
    "overconfident",
    "furtive",
    "stereotyped",
    "interesting",
    "unfasten",
    "frame",
    "sloppy",
    "hysterical",
    "jellyfish",
    "accept",
    "plants",
    "fasten",
    "front",
    "silky",
    "arch",
    "cushion",
    "terrific",
    "taboo",
    "veil",
    "fax",
    "expert",
    "pour",
    "pump",
    "succinct",
    "road",
    "spoon",
    "uttermost",
    "use",
    "ancient",
    "deadpan",
    "show",
    "dog",
    "degree",
    "utopian",
    "dapper",
    "irritate",
    "nice",
    "infamous",
    "vacuous",
    "scratch",
    "obnoxious",
    "odd",
    "swim",
    "communicate",
    "bloody",
    "relax",
    "town",
    "wanting",
    "bit",
    "action",
    "flat",
    "value",
    "surprise",
    "strengthen",
    "shape",
    "dirt",
    "complete",
    "funny",
    "railway",
    "weak",
    "camp",
    "hungry",
    "observant",
    "lick",
    "puffy",
    "hands",
    "ski",
    "remember",
    "questionable",
    "quill",
    "dock",
    "ship",
    "six",
    "heat",
    "mute",
    "behave",
    "basketball",
    "hammer",
    "mean",
    "reproduce",
    "ashamed",
    "harbor",
    "square",
    "week",
    "loss",
    "ruthless",
    "wine",
    "detail",
    "beneficial",
    "silk",
    "race",
    "needle",
    "discussion",
    "fine",
    "compare",
    "dramatic",
    "animated",
    "painstaking",
    "grass",
    "hug",
    "sweltering",
    "magnificent",
    "trees",
    "caption",
    "shrill",
    "lace",
    "fowl",
    "mourn",
    "eatable",
    "toes",
    "periodic",
    "behavior",
    "worry",
    "toothpaste",
    "whisper",
    "sock",
    "learn",
    "raise",
    "imaginary",
    "ignorant",
    "promise",
    "kettle",
    "wire",
    "blade",
    "flaky",
    "noisy",
    "jagged",
    "furniture",
    "crack",
    "discovery",
    "slimy",
    "solid",
    "release",
    "reach",
    "quiver",
    "throat",
    "beam",
    "glossy",
    "voyage",
    "type",
    "curl",
    "haircut",
    "driving",
    "melted",
    "comparison",
    "rose",
    "powder",
    "ask",
    "flippant",
    "humdrum",
    "far",
    "elfin",
    "attempt",
    "deeply",
    "many",
    "knot",
    "graceful",
    "visit",
    "decision",
    "destruction",
    "optimal",
    "country",
    "parsimonious",
    "guiltless",
    "quizzical",
    "fretful",
    "snail",
    "mundane",
    "crown",
    "mine",
    "increase",
    "motionless",
    "blind",
    "calm",
    "white",
    "narrow",
    "payment",
    "heavenly",
    "astonishing",
    "truthful",
    "airport",
    "box",
    "warn",
    "hurried",
    "stiff",
    "bizarre",
    "shaky",
    "cannon",
    "stitch",
    "wrestle",
    "stranger",
    "position",
    "tick",
    "blushing",
    "fact",
    "dizzy",
    "baseball",
    "better",
    "determined",
    "reduce",
    "ghost",
    "married",
    "jail",
    "rain",
    "doubtful",
    "analyze",
    "lumpy",
    "breezy",
    "party",
    "mammoth",
    "tenuous",
    "finicky",
    "naughty",
    "hook",
    "bite",
    "squealing",
    "allow",
    "rustic",
    "fluttering",
    "cast",
    "coat",
    "reflect",
    "envious",
    "confused",
    "market",
    "rot",
    "face",
    "reminiscent",
    "enchanted",
    "plucky",
    "zinc",
    "branch",
    "dark",
    "adjoining",
    "parallel",
    "curious",
    "secretary",
    "physical",
    "repulsive",
    "rotten",
    "lackadaisical",
    "lie",
    "explode",
    "receptive",
    "boot",
    "scissors",
    "carve",
    "untidy",
    "claim",
    "consist",
    "longing",
    "suspend",
    "grip",
    "electric",
    "fish",
    "limping",
    "group",
    "two",
    "difficult",
    "neighborly",
    "station",
    "nose",
    "chickens",
    "juvenile",
    "separate",
    "military",
    "noiseless",
    "cowardly",
    "drop",
    "vigorous",
    "moor",
    "able",
    "charge",
    "kaput",
    "crayon",
    "zealous",
    "assorted",
    "preserve",
    "switch",
    "nasty",
    "desert",
    "harass",
    "pink",
    "impolite",
    "curve",
    "plant",
    "possible",
    "hope",
    "groovy",
    "various",
    "file",
    "tax",
    "obeisant",
    "roasted",
    "nervous",
    "farm",
    "river",
    "scientific",
    "prick",
    "makeshift",
    "female",
    "oranges",
    "abounding",
    "filthy",
    "petite",
    "mug",
    "abortive",
    "guarded",
    "pass",
    "page",
    "addicted",
    "squeamish",
    "loud",
    "owe",
    "agreeable",
    "money",
    "spare",
    "signal",
    "kindhearted",
    "north",
    "heavy",
    "powerful",
    "help",
    "finger",
    "subsequent",
    "fade",
    "colorful",
    "womanly",
    "mysterious",
    "sassy",
    "toe",
    "color",
    "successful",
    "scream",
    "stone",
    "pizzas",
    "ajar",
    "growth",
    "rightful",
    "capricious",
    "nifty",
    "belligerent",
    "fumbling",
    "yellow",
    "hate",
    "tap",
    "endurable",
    "suppose",
    "obscene",
    "faint",
    "float",
    "wreck",
    "equable",
    "wasteful",
    "insidious",
    "quince",
    "ludicrous",
    "balance",
    "serve",
    "concentrate",
    "placid",
    "fanatical",
    "average",
    "twist",
    "soggy",
    "pocket",
    "craven",
    "old",
    "hanging",
    "aftermath",
    "adamant",
    "cold",
    "handy",
    "scold",
    "used",
    "copy",
    "jazzy",
    "decisive",
    "sheep",
    "cooperative",
    "squash",
    "soda",
    "dress",
    "gruesome",
    "refuse",
    "horse",
    "lumber",
    "awake",
    "elated",
    "obese",
    "shirt",
    "tiny",
    "lewd",
    "laborer",
    "ready",
    "zipper",
    "gratis",
    "notebook",
    "tasty",
    "blue-eyed",
    "scrub",
    "territory",
    "caring",
    "vast",
    "destroy",
    "death",
    "wind",
    "inform",
    "insect",
    "bells",
    "flower",
    "jolly",
    "laughable",
    "coil",
    "suffer",
    "knock",
    "amused",
    "eyes",
    "purple",
    "fire",
    "observation",
    "trust",
    "boat",
    "skate",
    "accessible",
    "carpenter",
    "industry",
    "distance",
    "detailed",
    "company",
    "lively",
    "rabbits",
    "gate",
    "slim",
    "yawn",
    "damp",
    "shivering",
    "memorize",
    "underwear",
    "troubled",
    "arrest",
    "pigs",
    "development",
    "announce",
    "tomatoes",
    "smell",
    "share",
    "doubt",
    "sign",
    "accidental",
    "large",
    "daffy",
    "army",
    "unit",
    "hilarious",
    "harm",
    "recognise",
    "drunk",
    "sense",
    "mighty",
    "nerve",
    "stir",
    "plough",
    "pointless",
    "hulking",
    "brawny",
    "exchange",
    "impartial",
    "badge",
    "blink",
    "illustrious",
    "welcome",
    "fearful",
    "handle",
)

private fun actuallyPickWord(): String {
    return WordBank[Random.nextInt(from = 0, until = WordBank.size)]
}

fun pickWords(count: Int = 3): List<String> {
    val pickedWord = mutableListOf<String>()
    var totalCount = count
    while (totalCount > 0) {
        val pickThis = actuallyPickWord()
        if (!pickedWord.contains(pickThis)) {
            pickedWord.add(pickThis)
            totalCount--
        }
    }
    return pickedWord.toList()
}
