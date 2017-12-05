/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javascriptdevil;

    public interface IProgress{
        void setOverrallProgress(float proportion);
        void setCurrentTaskProgress(float proportion);
        void setCurrentTaskMessage(String currentTaskMessage);
        void showError(String errorMessage);
        void done();
    }