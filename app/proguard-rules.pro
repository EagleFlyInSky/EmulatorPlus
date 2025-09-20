# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.eagle.emulator.MainHook


-dontwarn io.github.logtube.Logtube
-dontwarn io.github.logtube.core.IEventLogger
-dontwarn io.github.logtube.core.IMutableEvent
-dontwarn java.beans.Transient
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority
-dontwarn org.apache.logging.log4j.Level
-dontwarn org.apache.logging.log4j.LogManager
-dontwarn org.apache.logging.log4j.Logger
-dontwarn org.apache.logging.log4j.Marker
-dontwarn org.apache.logging.log4j.spi.AbstractLogger
-dontwarn org.bouncycastle.jce.provider.BouncyCastleProvider
-dontwarn org.jboss.logging.Logger$Level
-dontwarn org.jboss.logging.Logger
-dontwarn org.pmw.tinylog.Level
-dontwarn org.pmw.tinylog.LogEntryForwarder
-dontwarn org.pmw.tinylog.Logger
-dontwarn org.slf4j.ILoggerFactory
-dontwarn org.slf4j.Logger
-dontwarn org.slf4j.LoggerFactory
-dontwarn org.slf4j.Marker
-dontwarn org.slf4j.helpers.NOPLoggerFactory
-dontwarn org.slf4j.spi.LocationAwareLogger
-dontwarn org.tinylog.Level
-dontwarn org.tinylog.Logger
-dontwarn org.tinylog.configuration.Configuration
-dontwarn org.tinylog.format.AdvancedMessageFormatter
-dontwarn org.tinylog.format.MessageFormatter
-dontwarn org.tinylog.provider.LoggingProvider
-dontwarn org.tinylog.provider.ProviderRegistry