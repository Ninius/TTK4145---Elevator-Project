package com.gruppe78;

/**
 * Created by student on 12.02.16.
 */
public class Controller {
    public Controller(){
        Model model = new Model();
        model.setListener(new StateChanged() {
            @Override
            public void onFloorChanged() {

            }
        });
    }
}
