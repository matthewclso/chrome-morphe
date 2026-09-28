package app.matthew.chrome.patches

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference

val androidAutofillPatch = bytecodePatch(
    name = "Android autofill",
    description = "Allows Google's system autofill service in Chrome's Android autofill mode. Enable Autofill using another service in Chrome settings.",
    default = false,
) {
    compatibleWith(chromeCompatibility)
    dependsOn(settingsPatch)
    execute {
        requireTarget(packageMetadata)
        val provider = mutableClassDefBy("Lorg/chromium/chrome/browser/autofill/AutofillClientProviderUtils;")
        for (name in listOf("getAndroidAutofillFrameworkAvailability", "updatePackageUsedForAutofill")) {
            val method = provider.methods.single { it.name == name }
            check(method.parameterTypes.first() == "Lorg/chromium/components/prefs/PrefService;")
            check(method.hasString("autofill.third_party_package_used_for_platform_autofill"))
            val instructions = method.implementation!!.instructions.toList()
            val google = instructions.withIndex().single {
                ((it.value as? ReferenceInstruction)?.reference as? StringReference)?.string == "com.google.android.gms"
            }.index
            val equals = (instructions[google + 1] as? ReferenceInstruction)?.reference as? MethodReference
            check(equals?.toString() == "Ljava/lang/String;->equals(Ljava/lang/Object;)Z")
            val result = instructions[google + 2]
            check(result.opcode == Opcode.MOVE_RESULT && instructions[google + 3].opcode == Opcode.IF_EQZ)
            // Permit Google in the same path as other valid Android providers. Keep
            // enterprise policy, platform availability, explicit user selection,
            // web-origin data and the provider's authentication completely native.
            method.replaceInstruction(google + 2,
                "const/4 v${(result as OneRegisterInstruction).registerA}, 0x0")
        }
    }
}
