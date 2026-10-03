SKIPUNZIP=0

ui_print "************************************"
ui_print "       Aruack Music v1.0.1          "
ui_print "   Your Music. Your Way. - System   "
ui_print "************************************"

set_perm_recursive $MODPATH 0 0 0755 0644
set_perm $MODPATH/system/priv-app/AruackMusic/AruackMusic.apk 0 0 0644

ui_print "- Installation complete! Please reboot."
