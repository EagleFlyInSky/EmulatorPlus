package com.eagle.emulator.hook.beacon;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public final class AmStartCommandToIntentConverter {

    public static Intent createIntentByCommandArgs(String arguments, List<Pair<String, String>> tokenReplaceList) throws URISyntaxException {

        String[] argumentSplitted = arguments.split("[\\n\\s]+");

        ArrayList<String> argumentsReplacedAndSplit = new ArrayList<>(argumentSplitted.length);
        for (String s : argumentSplitted) {
            String string = s;
            for (Pair<String, String> it : tokenReplaceList) {
                string = string.replace(it.first, it.second);
            }
            argumentsReplacedAndSplit.add(string);
        }

        LinkedList<String> argumentLinkedList = new LinkedList<>(argumentsReplacedAndSplit);
        Intent intent = new Intent();
        Intent baseIntent = intent;
        boolean hasIntentInfo = false;
        Uri data = null;
        String type = null;
        while (!argumentLinkedList.isEmpty()) {
            String opt = argumentLinkedList.pop();
            switch (opt) {
                case "-a":
                    intent.setAction(argumentLinkedList.pop());
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-d":
                    data = Uri.parse(argumentLinkedList.pop());
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-t":
                    type = argumentLinkedList.pop();
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-i":
                    // Note: setIdentifier was added in API 29. Ensure your minSdkVersion supports it or use reflection.
                    intent.setIdentifier(argumentLinkedList.pop());
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-c":
                    intent.addCategory(argumentLinkedList.pop());
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-e":
                case "--es":
                    String key_es = argumentLinkedList.pop();
                    String value_es = argumentLinkedList.pop();
                    intent.putExtra(key_es, value_es);
                    break;
                case "--esn":
                    String key_esn = argumentLinkedList.pop();
                    intent.putExtra(key_esn, (String) null);
                    break;
                case "--ei":
                    String key_ei = argumentLinkedList.pop();
                    String value_ei = argumentLinkedList.pop();
                    intent.putExtra(key_ei, Integer.decode(value_ei));
                    break;
                case "--eu":
                    String key_eu = argumentLinkedList.pop();
                    String value_eu = argumentLinkedList.pop();
                    intent.putExtra(key_eu, Uri.parse(value_eu));
                    break;
                case "--ecn":
                    String key_ecn = argumentLinkedList.pop();
                    String value_ecn = argumentLinkedList.pop();
                    ComponentName cn = ComponentName.unflattenFromString(value_ecn);
                    if (cn == null) {
                        throw new IllegalArgumentException("Bad component name: " + value_ecn);
                    }
                    intent.putExtra(key_ecn, cn);
                    break;
                case "--eia":
                    String key_eia = argumentLinkedList.pop();
                    String value_eia = argumentLinkedList.pop();
                    String[] strings_eia = value_eia.split(",");
                    int[] list_eia = new int[strings_eia.length];
                    for (int i_eia = 0; i_eia < strings_eia.length; i_eia++) {
                        list_eia[i_eia] = Integer.decode(strings_eia[i_eia]);
                    }
                    intent.putExtra(key_eia, list_eia);
                    break;
                case "--eial":
                    String key_eial = argumentLinkedList.pop();
                    String value_eial = argumentLinkedList.pop();
                    String[] strings_eial = value_eial.split(",");
                    ArrayList<Integer> list_eial = new ArrayList<>(strings_eial.length);
                    for (int i_eial = 0; i_eial < strings_eial.length; i_eial++) {
                        list_eial.add(Integer.decode(strings_eial[i_eial]));
                    }
                    intent.putExtra(key_eial, list_eial);
                    break;
                case "--el":
                    String key_el = argumentLinkedList.pop();
                    String value_el = argumentLinkedList.pop();
                    intent.putExtra(key_el, Long.valueOf(value_el));
                    break;
                case "--ela":
                    String key_ela = argumentLinkedList.pop();
                    String value_ela = argumentLinkedList.pop();
                    String[] strings_ela = value_ela.split(",");
                    long[] list_ela = new long[strings_ela.length];
                    for (int i_ela = 0; i_ela < strings_ela.length; i_ela++) {
                        list_ela[i_ela] = Long.valueOf(strings_ela[i_ela]);
                    }
                    intent.putExtra(key_ela, list_ela);
                    hasIntentInfo = true;
                    break;
                case "--elal":
                    String key_elal = argumentLinkedList.pop();
                    String value_elal = argumentLinkedList.pop();
                    String[] strings_elal = value_elal.split(",");
                    ArrayList<Long> list_elal = new ArrayList<>(strings_elal.length);
                    for (int i_elal = 0; i_elal < strings_elal.length; i_elal++) {
                        list_elal.add(Long.valueOf(strings_elal[i_elal]));
                    }
                    intent.putExtra(key_elal, list_elal);
                    hasIntentInfo = true;
                    break;
                case "--ef":
                    String key_ef = argumentLinkedList.pop();
                    String value_ef = argumentLinkedList.pop();
                    intent.putExtra(key_ef, Float.valueOf(value_ef));
                    hasIntentInfo = true;
                    break;
                case "--efa":
                    String key_efa = argumentLinkedList.pop();
                    String value_efa = argumentLinkedList.pop();
                    String[] strings_efa = value_efa.split(",");
                    float[] list_efa = new float[strings_efa.length];
                    for (int i_efa = 0; i_efa < strings_efa.length; i_efa++) {
                        list_efa[i_efa] = Float.valueOf(strings_efa[i_efa]);
                    }
                    intent.putExtra(key_efa, list_efa);
                    hasIntentInfo = true;
                    break;
                case "--efal":
                    String key_efal = argumentLinkedList.pop();
                    String value_efal = argumentLinkedList.pop();
                    String[] strings_efal = value_efal.split(",");
                    ArrayList<Float> list_efal = new ArrayList<>(strings_efal.length);
                    for (int i_efal = 0; i_efal < strings_efal.length; i_efal++) {
                        list_efal.add(Float.valueOf(strings_efal[i_efal]));
                    }
                    intent.putExtra(key_efal, list_efal);
                    hasIntentInfo = true;
                    break;
                case "--esa":
                    String key_esa = argumentLinkedList.pop();
                    String value_esa = argumentLinkedList.pop();
                    // Split on commas unless they are preceeded by an escape.
                    // The escape character must be escaped for the string and
                    // again for the regex, thus four escape characters become one.
                    String[] strings_esa = value_esa.split("(?<!\\\\),");
                    intent.putExtra(key_esa, strings_esa);
                    hasIntentInfo = true;
                    break;
                case "--esal":
                    String key_esal = argumentLinkedList.pop();
                    String value_esal = argumentLinkedList.pop();
                    // Split on commas unless they are preceeded by an escape.
                    // The escape character must be escaped for the string and
                    // again for the regex, thus four escape characters become one.
                    String[] strings_esal = value_esal.split("(?<!\\\\),");
                    ArrayList<String> list_esal = new ArrayList<>(strings_esal.length);
                    for (int i_esal = 0; i_esal < strings_esal.length; i_esal++) {
                        list_esal.add(strings_esal[i_esal]);
                    }
                    intent.putExtra(key_esal, list_esal);
                    hasIntentInfo = true;
                    break;
                case "--ez":
                    String key_ez = argumentLinkedList.pop();
                    String value_ez = argumentLinkedList.pop().toLowerCase(Locale.getDefault());
                    // Boolean.valueOf() results in false for anything that is not "true", which is
                    // error-prone in shell commands
                    boolean arg_ez;
                    if ("true".equals(value_ez) || "t".equals(value_ez)) {
                        arg_ez = true;
                    } else if ("false".equals(value_ez) || "f".equals(value_ez)) {
                        arg_ez = false;
                    } else {
                        try {
                            arg_ez = Integer.decode(value_ez) != 0;
                        } catch (NumberFormatException ex) {
                            throw new IllegalArgumentException("Invalid boolean value: " + value_ez);
                        }
                    }
                    intent.putExtra(key_ez, arg_ez);
                    break;
                case "-n":
                    String str_n = argumentLinkedList.pop();
                    ComponentName cn_n = ComponentName.unflattenFromString(str_n);
                    if (cn_n == null) {
                        throw new IllegalArgumentException("Bad component name: " + str_n);
                    }
                    intent.setComponent(cn_n);
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-p":
                    String str_p = argumentLinkedList.pop();
                    intent.setPackage(str_p);
                    if (intent == baseIntent) hasIntentInfo = true;
                    break;
                case "-f":
                    String str_f = argumentLinkedList.pop();
                    intent.setFlags(Integer.decode(str_f));
                    break;
                case "--grant-read-uri-permission":
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    break;
                case "--grant-write-uri-permission":
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    break;
                case "--grant-persistable-uri-permission":
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    break;
                case "--grant-prefix-uri-permission":
                    intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    break;
                case "--exclude-stopped-packages":
                    intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
                    break;
                case "--include-stopped-packages":
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    break;
                case "--debug-log-resolution":
                    intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
                    break;
                case "--activity-brought-to-front":
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    break;
                case "--activity-clear-top":
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case "--activity-clear-when-task-reset":
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    break;
                case "--activity-exclude-from-recents":
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    break;
                case "--activity-launched-from-history":
                    intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                    break;
                case "--activity-multiple-task":
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    break;
                case "--activity-no-animation":
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                case "--activity-no-history":
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    break;
                case "--activity-no-user-action":
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    break;
                case "--activity-previous-is-top":
                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    break;
                case "--activity-reorder-to-front":
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    break;
                case "--activity-reset-task-if-needed":
                    intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    break;
                case "--activity-single-top":
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    break;
                case "--activity-clear-task":
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "--activity-task-on-home":
                    intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    break;
                case "--activity-match-external":
                    intent.addFlags(Intent.FLAG_ACTIVITY_MATCH_EXTERNAL);
                    break;
                case "--receiver-registered-only":
                    intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                    break;
                case "--receiver-replace-pending":
                    intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
                    break;
                case "--receiver-foreground":
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    break;
                case "--receiver-no-abort":
                    intent.addFlags(Intent.FLAG_RECEIVER_NO_ABORT);
                    break;
                case "--selector":
                    intent.setDataAndType(data, type);
                    intent = new Intent();
                    break;

                // Ignore below "am start" arguments key and pop value too.
                case "-P":
                case "--user":
                case "--start-profiler":
                case "--sampling":
                case "--attach-agent":
                case "--attach-agent-bind":
                case "-R":
                case "--receiver-permission":
                case "--display":
                case "--windowingMode":
                case "--activityType":
                case "--task":
                    argumentLinkedList.pop();
                    break;
                // Ignore below singular "am start" arguments key.
                case "":
                case "-D":
                case "-N":
                case "-W":
                case "-S":
                case "--streaming":
                case "--track-allocation":
                case "--task-overlay":
                case "--lock-task":
                case "--allow-background-activity-starts":
                    break;

                default:
                    throw new IllegalArgumentException("Unknown option: " + opt);
            }
        }
        intent.setDataAndType(data, type);
        boolean hasSelector = intent != baseIntent;
        if (hasSelector) {
            // A selector was specified; fix up.
            baseIntent.setSelector(intent);
            intent = baseIntent;
        }
        String arg = argumentLinkedList.isEmpty() ? null : argumentLinkedList.pop();
        baseIntent = null;
        if (arg == null) {
            if (hasSelector) {
                // If a selector has been specified, and no arguments
                // have been supplied for the main Intent, then we can
                // assume it is ACTION_MAIN CATEGORY_LAUNCHER; we don't
                // need to have a component name specified yet, the
                // selector will take care of that.
                baseIntent = new Intent(Intent.ACTION_MAIN);
                baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            }
        } else if (arg.indexOf(':') >= 0) {
            // The argument is a URI.  Fully parse it, and use that result
            // to fill in any data not specified so far.
            baseIntent = Intent.parseUri(arg, Intent.URI_INTENT_SCHEME | Intent.URI_ANDROID_APP_SCHEME | Intent.URI_ALLOW_UNSAFE);
        } else if (arg.indexOf('/') >= 0) {
            // The argument is a component name.  Build an Intent to launch
            // it.
            baseIntent = new Intent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            baseIntent.setComponent(ComponentName.unflattenFromString(arg));
        } else {
            // Assume the argument is a package name.
            baseIntent = new Intent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            baseIntent.setPackage(arg);
        }
        if (baseIntent != null) {
            Bundle extras = intent.getExtras();
            intent.replaceExtras((Bundle) null);
            Bundle uriExtras = baseIntent.getExtras();
            baseIntent.replaceExtras((Bundle) null);
            if (intent.getAction() != null && baseIntent.getCategories() != null) {
                HashSet<String> cats = new HashSet<>(baseIntent.getCategories());
                for (String c : cats) {
                    baseIntent.removeCategory(c);
                }
            }
            intent.fillIn(baseIntent, Intent.FILL_IN_COMPONENT | Intent.FILL_IN_SELECTOR);
            if (extras == null) {
                extras = uriExtras;
            } else if (uriExtras != null) {
                uriExtras.putAll(extras);
                extras = uriExtras;
            }
            intent.replaceExtras(extras);
            hasIntentInfo = true;
        }
        if (!hasIntentInfo) {
            throw new IllegalArgumentException("No intent supplied");
        }
        // Add addition flags for player.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    // You will need to define a Pair class or use android.util.Pair if available.
    // A simple implementation is provided here if needed.
    public static final class Pair<F, S> {
        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}