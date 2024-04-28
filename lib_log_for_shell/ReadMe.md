

两个脚本, `.logger.sh` shell 日志工具

```shell
source logger.sh

    colorEcho      \      # 带颜色的 echo
      -fg "$Red"   \      # 前景色
      -bg "$Green" \      # 背景色
      -ul "$True"  \      # 带有下划线与否
      -msg "[[[[[[我是消息体]]]]]]"

    log_i '我是一条 log_i 消息'
    log_d '我是一条 log_d 消息'
    log_w '我是一条 log_w 消息'
    log_e '我是一条 log_e 消息'

```

两个脚本, `.logger.mk` makefile 日志工具

```makefile

include logger.mk

example:
	$(call LogE, "log_e: makefile-日志\\n换行文本")
	@sleep 1
	$(call LogW, "log_w: makefile-日志换行文本")
	@sleep 1
	$(call LogD, "log_d: makefile-日志\\n换行文本")
	$(call LogI, "log_i: makefile-日志\\n换行文本")
```