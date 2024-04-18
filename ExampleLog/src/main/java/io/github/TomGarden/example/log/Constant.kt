package io.github.TomGarden.example.log

import android.content.Context

object Constant {
     fun Context.getAppDefLogPath() =
         externalCacheDir?.path ?:
         cacheDir.path ?:
         "/data/data/io.github.TomGarden.example.log/tom_custom/cache"
}