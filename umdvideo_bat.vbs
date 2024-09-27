If WScript.Arguments.Count = 0 Then
    WScript.Echo "Missing parameters!"
Else
    Set WshShell = CreateObject("WScript.Shell") 
    WshShell.Run chr(34) & WScript.Arguments.Item(0) & chr(34) & chr(32) & chr(34) & Wscript.Arguments.Item(1) & Chr(34), 0
    Set WshShell = Nothing
End If