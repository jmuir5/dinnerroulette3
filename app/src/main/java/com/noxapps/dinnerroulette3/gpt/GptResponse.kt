package com.noxapps.dinnerroulette3.gpt
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * object classes for gpt responses
 */

@Serializable
class GptResponse(
    val id:String,
    @JsonNames("object")val type:String,
    val created:Int,
    val model:String,
    val choices:List<GptChoices>,
    val usage: GptUsage
)

@Serializable
class GptChoices(
    val index:Int,
    val message: GptMessage,
    val finish_reason:String
)

@Serializable
class GptMessage(
    val role:String,
    val content:String
)

@Serializable
class GptUsage(
    val prompt_tokens:Int,
    val completion_tokens:Int,
    val total_tokens:Int
)

@Serializable
class GptImageResponse(
    val created:Int,
    val data:List<ImageLink>
)

@Serializable
class ImageLink(
    val url:String
)
