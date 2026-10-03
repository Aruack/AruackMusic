# Aruack Music - Magisk Installer
ui_print "****************************************"
ui_print "*            ARUACK MUSIC              *"
ui_print "*   Serverless, Legal, Ad-Free Player  *"
ui_print "****************************************"
ui_print "- Installing Aruack Music system priv-app..."

# Set permissions
set_perm_recursive $MODPATH 0 0 0755 0644
set_perm_recursive $MODPATH/system/priv-app/AruackMusic 0 0 0755 0644

ui_print "- Installation complete! Reboot to apply."
