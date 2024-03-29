package io.github.tomgarden.lib.lite_log;

/**
 * A proxy interface to enable additional operations.
 * <p>
 * 一个允许增加额外操作的代理接口
 * <p>
 * Contains all possible Log message usages.
 * <p>
 * 包含所有的日志用法
 */
public abstract class TomPrinter {

    /*默认控制台日志策略*/
    TomLogStrategy defLogcatStrategy = null;

    /*临时控制台日志策略*/
    TomLogStrategy temporaryLogcatStrategy = null;

    /*默认磁盘日志策略*/
    TomLogStrategy defDiskStrategy = null;

    /*临时磁盘日志策略*/
    TomLogStrategy temporaryDiskStrategy = null;

    public TomLogStrategy getDefLogcatStrategy() {
        return defLogcatStrategy;
    }

    public void setDefLogcatStrategy(TomLogStrategy defLogcatStrategy) {
        this.defLogcatStrategy = defLogcatStrategy;
    }

    public TomLogStrategy getTemporaryLogcatStrategy() {
        return temporaryLogcatStrategy;
    }

    public void setTemporaryLogcatStrategy(TomLogStrategy temporaryLogcatStrategy) {
        this.temporaryLogcatStrategy = temporaryLogcatStrategy;
    }

    public TomLogStrategy getDefDiskStrategy() {
        return defDiskStrategy;
    }

    public void setDefDiskStrategy(TomLogStrategy defDiskStrategy) {
        this.defDiskStrategy = defDiskStrategy;
    }

    public TomLogStrategy getTemporaryDiskStrategy() {
        return temporaryDiskStrategy;
    }

    public void setTemporaryDiskStrategy(TomLogStrategy temporaryDiskStrategy) {
        this.temporaryDiskStrategy = temporaryDiskStrategy;
    }

    abstract TomLogStrategy unNullTemporaryLogcatStrategy();

    abstract void d(String message, Object... args);

    abstract void d(Object any);

    abstract void e(Boolean withSingleFile, String message, Object... args);

    abstract void e(Throwable throwable, Boolean withSingleFile, String message, Object... args);

    abstract void w(String message, Object... args);

    abstract void i(String message, Object... args);

    abstract void v(String message, Object... args);

    abstract void wtf(String message, Object... args);

    abstract void log(int priority, String message, Throwable throwable, boolean withSingleFile);

    abstract void clearLogStrategies();
}
