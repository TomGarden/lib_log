#!/bin/bash

####################################################################################################
# reference :
#   1. https://stackoverflow.com/q/5947742/7707781
#   1. https://lingsamuel.github.io/page/shell-utils/
####################################################################################################


#         |  FG  |	BG     |   Name                   |
#         |------+---------+--------------------------|
#         |  30  |	40     |   Black                  |
#         |  31  |	41     |   Red                    |
#         |  32  |	42     |   Green                  |
#         |  33  |	43     |   Yellow                 |
#         |  34  |	44     |   Blue                   |
#         |  35  |	45     |   Magenta                |
#         |  36  |	46     |   Cyan                   |
#         |  37  |	47     |   White                  |
#         |  90  |	100    |   Bright Black  (Gray)   |
#         |  91  |	101    |   Bright Red             |
#         |  92  |	102    |   Bright Green           |
#         |  93  |	103    |   Bright Yellow          |
#         |  94  |	104    |   Bright Blue            |
#         |  95  |	105    |   Bright Magenta         |
#         |  96  |	106    |   Bright Cyan            |
#         |  97  |	107    |   Bright White           |

# region variable
True="true"
False="false"
Zero="0"

Off_Color='\033[0m'

Black="Black"
Red="Red"
Green="Green"
Yellow="Yellow"
Blue="Blue"
Magenta="Magenta"
Cyan="Cyan"
White="White"
Bright_Black="Bright Black"
Bright_Red="Bright Red"
Bright_Green="Bright Green"
Bright_Yellow="Bright Yellow"
Bright_Blue="Bright Blue"
Bright_Magenta="Bright Magenta"
Bright_Cyan="Bright Cyan"
Bright_White="Bright White"

TAG="TomYeo"
file="${BASH_SOURCE[1]}"  # 获取调用函数的文件路径
line="${BASH_LINENO[0]}"  # 获取调用函数的行数

# endregion variable

# 获取调用信息 TIME TAG 文件:行 #函数
#            message
#function callInfo() {
#
#}

# 真正的打印动作
function colorEcho() {
    local fg
    local bg
    local ul=$False
    local msg
    local errMsg

    function errMsgAppend(){
      errMsg="$errMsg$1\n"
    }


    # 解析参数
    while [[ "$#" -gt 0 ]]; do
        case "$1" in
            -fg|--foreground)
                fg="$2"
                shift 2
                ;;
            -bg|--background)
                bg="$2"
                shift 2
                ;;
            -ul|--underline)
                ul="$2"
                shift 2
                ;;
            -msg|--message)
                msg="$2"
                shift 2
                ;;
            *)
                echo "CMD ERR: $1" >&2
                return 1
                ;;
        esac
    done

    #region fg init
    case "$fg" in
      30|31|32|33|34|35|36|37|90|91|92|93|94|95|96|97) ;;
      "$Black")           fg=30 ;;
      "$Red")             fg=31 ;;
      "$Green")           fg=32 ;;
      "$Yellow")          fg=33 ;;
      "$Blue")            fg=34 ;;
      "$Magenta")         fg=35 ;;
      "$Cyan")            fg=36 ;;
      "$White")           fg=37 ;;
      "$Bright_Black")    fg=90 ;;
      "$Bright_Red")      fg=91 ;;
      "$Bright_Green")    fg=92 ;;
      "$Bright_Yellow")   fg=93 ;;
      "$Bright_Blue")     fg=94 ;;
      "$Bright_Magenta")  fg=95 ;;
      "$Bright_Cyan")     fg=96 ;;
      "$Bright_White")    fg=97 ;;
      *)
        fg=$Zero
        errMsgAppend "Unknown fg param $fg for command colorEcho"
        ;;
    esac
    #endregion fg init

    #region bg init
    case "$bg" in
      40|41|42|43|44|45|46|47|100|101|102|103|104|105|106|107) ;;
      "$Black")           bg=40   ;;
      "$Red")             bg=41   ;;
      "$Green")           bg=42   ;;
      "$Yellow")          bg=43   ;;
      "$Blue")            bg=44   ;;
      "$Magenta")         bg=45   ;;
      "$Cyan")            bg=46   ;;
      "$White")           bg=47   ;;
      "$Bright_Black")    bg=100  ;;
      "$Bright_Red")      bg=101  ;;
      "$Bright_Green")    bg=102  ;;
      "$Bright_Yellow")   bg=103  ;;
      "$Bright_Blue")     bg=104  ;;
      "$Bright_Magenta")  bg=105  ;;
      "$Bright_Cyan")     bg=106  ;;
      "$Bright_White")    bg=107  ;;
      *)
        bg=$Zero
        errMsgAppend "Unknown bg param $bg for command colorEcho"
        ;;
    esac
    #endregion bg init

    # region ul init
    if [[ "$ul" == "" || "$ul" == "$False" ]]; then
        ul=$False
    else
        ul=$True
    fi
    # endregion ul init

    # region msg init
    if [[ "$msg" == "" ]]; then
        ul="Empty msg content"
    fi
    # endregion msg init

    local format="\033["

    function formatAppend() {
      if [[ $format == *'[' ]]; then
        format=$format$1
      else
        format=$format";"$1
      fi
    }

    function formatAppendEnd() {
      if [[ $format == *'[' ]]; then
        format=$format$Zero"m"
      else
        format=$format"m"
      fi
    }



    if [[ "$ul" == "$True" ]]; then
      formatAppend '4'
    fi

    if [[ "$bg" != "$Zero" ]]; then
      formatAppend "$bg"
    fi

    if [[ "$fg" != "$Zero" ]]; then
      formatAppend "$fg"
    fi

    formatAppendEnd

    local Current_Datetime=$(date +"%Y-%m-%d %H:%M:%S %Z")
    echo "$format $msg $Off_Color"
    echo -n -e "$format$Current_Datetime $TAG  $msg$Off_Color\n"
    sleep 3
}

function log_i() {
  colorEcho -fg "$Zero" -msg "$1"
}
function log_d() {
  colorEcho -fg "$Green" -msg "$1"
}
function log_w() {
  colorEcho -fg "$Yellow" -msg "$1"
}

function log_e() {
  colorEcho -fg "$Red" -msg "$1"
}



#function testEcho() {
#    colorEcho      \      # 带颜色的 echo
#      -fg "$Red"   \      # 前景色
#      -bg "$Green" \      # 背景色
#      -ul "$True"  \      # 带有下划线与否
#      -msg "[[[[[[我是消息体]]]]]]"
#
#    log_i '我是一条 log_i 消息'
#    log_d '我是一条 log_d 消息'
#    log_w '我是一条 log_w 消息'
#    log_e '我是一条 log_e 消息'
#}
#
#testEcho