import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

fun getMap(): ImmutableMap<String, String> {
    return mapOf("key" to "value").toImmutableMap()
}
