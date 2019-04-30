package com.ripe.android.plugins

import com.ripe.android.base.ObservableCallback
import com.ripe.android.base.Ripe

/**
 * Plugin responsible for applying synchronization rules.
 *
 * @property options A map with options to configure the plugin.
 * @constructor Constructor for the SyncPlugin.
 *
 * @param rulesMap A Map with synchronization rules to be applied.
 * If defined, overrides the rules defined on the model's config.
 */
class SyncPlugin constructor(rulesMap: Map<String, Any>? = null, override var options: Map<String, Any> = HashMap()): Plugin(options) {

    private var rules = this.normalizeRules(rulesMap)
    private val manual = options["manual"] as? Boolean ?: rulesMap != null
    private val auto = !this.manual
    private var postConfigBind: ObservableCallback? = null
    private var partBind: ObservableCallback? = null

    override fun register(owner: Ripe) {
        super.register(owner)


        if (this.auto && owner.loadedConfig != null) {
            @Suppress("unchecked_cast")
            val rules = owner.loadedConfig!!["sync"] as Map<String, Any>
            this.rules = this.normalizeRules(rules)
        }

        this.postConfigBind = if (this.manual) null else owner.bind("post_config") {
            @Suppress("unchecked_cast")
            val rules = it["sync"] as? Map<String, Any>
            if (rules != null) {
                this.rules = this.normalizeRules(rules)
            }
        }

        this.partBind = owner.bind("part") {
            val name = it["part"] as? String
            @Suppress("unchecked_cast")
            val value = it["value"] as? Map<String, String?>
            this.applySync(name, value)
        }
    }

    override fun unregister() {
        this.owner?.unbind("part", this.partBind)
        this.owner?.unbind("post_config", this.postConfigBind)

        super.unregister()
    }

    /**
     * @suppress
     */
    private fun normalizeRules(rules: Map<String, Any>?): Map<String, Any> {
        val result = HashMap<String, Any>()
        if (rules == null) {
            return result
        }

        for((ruleName, value) in rules) {
            @Suppress("unchecked_cast")
            val rule = (value as List<Any>).toMutableList()
            for (index in 0 until rule.size) {
                val part = rule[index]
                if (part is String) {
                    rule[index] = mapOf("part" to part)
                }
            }
            result[ruleName] = rule
        }
        return result
    }

    /**
     * @suppress
     */
    private fun applySync(partName: String?, partValue: Map<String, String?>?) {
        for ((key, _) in this.rules) {
            // if a part was selected and it is part of
            // the rule then its value is used otherwise
            // the first part of the rule is used
            @Suppress("unchecked_cast")
            val rule = this.rules[key] as List<Map<String, String>>
            val firstPart = rule[0]
            val name = partName ?: firstPart["part"]
            @Suppress("unchecked_cast")
            val value = partValue ?: this.owner!!.parts[name] as Map<String, String>

            // checks if the part triggers the sync rule
            // and skips to the next rule if it doesn't
            if (!this.shouldSync(rule, name!!, value)) {
                continue
            }

            // iterates through the parts of the rule and
            // sets their material and color according to
            // the sync rule in case there's a match
            rule.forEach {
                // in case the current rule definition references the current
                // part in rule definition, ignores the current loop
                val part = it["part"] as String
                val material = it["material"]
                val color = it["color"]
                if (part == name) {
                    return@forEach
                }

                // tries to find the target part configuration an in case
                // no such part is found throws an error
                @Suppress("unchecked_cast")
                val target = this.owner!!.parts[part] as? Map<String, Any>
                target ?: throw Error("Target part for rule not found $part")
                val result = target.toMutableMap()
                if (color == null) {
                    result["material"] = material ?: value["material"] as String
                }
                result["color"] = color ?: value["color"] as String
                this.owner!!.parts[part] = result
            }
        }
    }

    /**
     * @suppress
     */
    private fun shouldSync(rule: List<Map<String, String>>, name: String, value: Map<String, String?>): Boolean {
        rule.forEach {
            val part = it["part"]
            val material = it["material"]
            val color = it["color"]
            val materialSync = material == null || material == value["material"]
            val colorSync = color == null || color == value["color"]
            if (part == name && materialSync && colorSync) {
                return true
            }
        }
        return false
    }
}