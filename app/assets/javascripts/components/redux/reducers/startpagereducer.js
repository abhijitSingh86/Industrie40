import {
    SETUP_FOR_RERUN,
    SETUP_FOR_RERUN_FAIL,
    SETUP_FOR_CLONE,
    SETUP_FOR_CLONE_FAIL} from "../actions/startpageaction";


const initialState = {

    responseMsg:""
};

export default function startpage(state = initialState , action){

    switch(action.type){
        case "reset" :{
            return initialState;
        }
        case SETUP_FOR_RERUN: {

            var state = {...state,responseMsg:"Previous Data reset complete.. starting monitoring mode" };

            return state;
        }

        case SETUP_FOR_RERUN_FAIL: {

            var state = {...state,responseMsg:"Error during previous data clear. <br/>"+action.payload }
            return state;
        }

        case SETUP_FOR_CLONE_FAIL:{
            var state = {...state,responseMsg:"Error retrieving previous simulation data.<br/>"+action.payload};
            return state;
        }

        case SETUP_FOR_CLONE:{
            var state = {...state,responseMsg:"Clone data retrieved.. loading registration panel."}
            return state;
        }
        default:
            return state;

    }
}