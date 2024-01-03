package com.example.campushive.util

import kotlin.random.Random

class GameUtil {

    fun getWord(): String {
        var words = arrayOf(
            "Elephant",
            "Sunshine",
            "Guitar",
            "Rainbow",
            "Pizza",
            "Bicycle",
            "Moonlight",
            "Butterfly",
            "Castle",
            "Ocean",
            "Chocolate",
            "Treehouse",
            "Fireworks",
            "Adventure",
            "Watermelon",
            "Dragonfly",
            "Treasure",
            "Starry Night",
            "Cupcake"
        )

        return words[getRandomNumberInRange()].lowercase()

    }

    private fun getRandomNumberInRange(): Int {
        return Random.nextInt(19) // Generates a random number between 0 and 18 (inclusive)
    }
}