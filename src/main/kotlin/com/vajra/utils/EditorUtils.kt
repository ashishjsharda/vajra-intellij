package com.vajra.utils

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

object EditorUtils {
    
    fun getActiveEditor(project: Project): Editor? {
        return FileEditorManager.getInstance(project).selectedTextEditor
    }
    
    fun getSelectedText(project: Project): String? {
        val editor = getActiveEditor(project) ?: return null
        return editor.selectionModel.selectedText
    }
    
    fun getCurrentFileContent(project: Project): String? {
        val editor = getActiveEditor(project) ?: return null
        return editor.document.text
    }
    
    fun getLanguage(project: Project): String? {
        val editor = getActiveEditor(project) ?: return null
        val file = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        return file?.fileType?.name
    }
}
