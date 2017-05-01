/**
 * Created by billa on 14.04.17.
 */
import React from 'react';
import TableView from "./TableView.jsx";
import "bootstrap/dist/css/bootstrap.css";

class Form extends React.Component {

    render(){
        return (<div>
            <form class="form-horizontal">
                <fieldset>

                    <legend>Simulation Manager</legend>

                    <div class="form-group">
                        <label class="col-md-4 control-label" for="textinput">Simulation Name</label>
                        <div class="col-md-4">
                            <input id="textinput" name="textinput" type="text" placeholder="name" class="form-control input-md" required=""/>
                                <span class="help-block">Unique Name to use in future</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-4 control-label" for="next"></label>
                        <div class="col-md-4">
                            <button id="next" name="next" class="btn btn-primary">next</button>
                        </div>
                    </div>

                </fieldset>
            </form>

            <TableView/>
            </div>
        );
    }
}

export default Form;