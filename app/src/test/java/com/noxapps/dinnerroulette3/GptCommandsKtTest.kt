package com.noxapps.dinnerroulette3

import com.noxapps.dinnerroulette3.gpt.GptResponse
import com.noxapps.dinnerroulette3.input.ParsedResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

internal class  GptCommandsKtTest{

    val sampleGptResponseText = """{
        "id": "chatcmpl-7mAEgJEZKGupS4W0YlnkoavvpPE2Y",
        "object": "chat.completion",
        "created": 1691713446,
        "model": "gpt-3.5-turbo-0613",
        "choices": [
        {
            "index": 0,
            "message": {
            "role": "assistant",
            "content": "[title]Piquant Vietnamese Beef Stir Fry\n[desc]This Piquant Vietnamese Beef Stir Fry is a delicious and flavorful dish that combines tender beef slices with aromatic herbs and spices. The dish has a perfect balance of heat and tanginess, making it a satisfying and hearty meal.\n[ingredients]\n- 400g beef sirloin, thinly sliced\n- 3 cloves of garlic, minced\n- 1 red chili, thinly sliced\n- 1 onion, thinly sliced\n- 1 bell pepper, thinly sliced\n- 2 tablespoons fish sauce\n- 2 tablespoons soy sauce\n- 2 tablespoons lime juice\n- 1 tablespoon brown sugar\n- 1 tablespoon vegetable oil\n- Fresh mint leaves, for garnish\n- Cooked rice or rice noodles, for serving\n\n[method]\n1. In a bowl, combine the fish sauce, soy sauce, lime juice, and brown sugar. Mix well to dissolve the sugar.\n2. Heat the vegetable oil in a wok or large skillet over high heat. Add the minced garlic and sliced chili, and stir-fry for about 30 seconds until fragrant.\n3. Add the beef slices to the wok and stir-fry for 2-3 minutes until they are cooked to your liking. Remove the beef from the wok and set it aside.\n4. In the same wok, add the onion and bell pepper slices. Stir-fry for 2-3 minutes until they start to soften and caramelize.\n5. Return the cooked beef to the wok and pour in the sauce mixture. Stir-fry for another minute to coat the beef and vegetables with the sauce.\n6. Remove from heat and garnish with fresh mint leaves.\n7. Serve the Piquant Vietnamese Beef Stir Fry with cooked rice or rice noodles.\n\n[notes] \n- Adjust the amount of chili according to your preferred level of spiciness.\n- Feel free to add other vegetables such as carrots, broccoli, or bean sprouts for extra texture and flavor.\n- You can marinate the beef in the sauce mixture for 20-30 minutes before cooking for more intense flavors.\n\n[image] This Vietnamese Beef Stir Fry is a vibrant and colorful dish. It features tender slices of beef coated in a flavorful sauce, accompanied by the inviting aroma of garlic, chili, and fresh mint. Serve it alongside a bed of steamed jasmine rice or rice noodles for a complete and satisfying meal."
        },
            "finish_reason": "stop"
        }
        ],
        "usage": {
        "prompt_tokens": 158,
        "completion_tokens": 498,
        "total_tokens": 656
    }
    }
    """
    val sampleGptResponse = Json.decodeFromString<GptResponse>(sampleGptResponseText)
    val input = "[title] A [desc] B [ingredients] C [method] D [notes] E [image]F"



    @Test
    fun parseResponse() {
        val expectedOutput = ParsedResponse(
            "Piquant Vietnamese Beef Stir Fry",
            "This Piquant Vietnamese Beef Stir Fry is a delicious and flavorful dish that combines tender beef slices with aromatic herbs and spices. The dish has a perfect balance of heat and tanginess, making it a satisfying and hearty meal.",
            "- 400g beef sirloin, thinly sliced\\n- 3 cloves of garlic, minced\\n- 1 red chili, thinly sliced\\n- 1 onion, thinly sliced\\n- 1 bell pepper, thinly sliced\\n- 2 tablespoons fish sauce\\n- 2 tablespoons soy sauce\\n- 2 tablespoons lime juice\\n- 1 tablespoon brown sugar\\n- 1 tablespoon vegetable oil\\n- Fresh mint leaves, for garnish\\n- Cooked rice or rice noodles, for serving",
            "1. In a bowl, combine the fish sauce, soy sauce, lime juice, and brown sugar. Mix well to dissolve the sugar.\\n2. Heat the vegetable oil in a wok or large skillet over high heat. Add the minced garlic and sliced chili, and stir-fry for about 30 seconds until fragrant.\\n3. Add the beef slices to the wok and stir-fry for 2-3 minutes until they are cooked to your liking. Remove the beef from the wok and set it aside.\\n4. In the same wok, add the onion and bell pepper slices. Stir-fry for 2-3 minutes until they start to soften and caramelize.\\n5. Return the cooked beef to the wok and pour in the sauce mixture. Stir-fry for another minute to coat the beef and vegetables with the sauce.\\n6. Remove from heat and garnish with fresh mint leaves.\\n7. Serve the Piquant Vietnamese Beef Stir Fry with cooked rice or rice noodles.",
            "- Adjust the amount of chili according to your preferred level of spiciness.\\n- Feel free to add other vegetables such as carrots, broccoli, or bean sprouts for extra texture and flavor.\\n- You can marinate the beef in the sauce mixture for 20-30 minutes before cooking for more intense flavors.",
            "This Vietnamese Beef Stir Fry is a vibrant and colorful dish. It features tender slices of beef coated in a flavorful sauce, accompanied by the inviting aroma of garlic, chili, and fresh mint. Serve it alongside a bed of steamed jasmine rice or rice noodles for a complete and satisfying meal.")
        assertEquals(expectedOutput,
            com.noxapps.dinnerroulette3.gpt.parseResponse(sampleGptResponse)
        )
    }

    @Test
    fun generatePrompt() {
    }
}