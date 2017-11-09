import React from 'react';
import ReactDom from 'react-dom';
import MainComponent from './components/MainComponent';
import {Provider} from "react-redux";

import store, { history } from './components/redux/store/configureStore'
import '../stylesheets/style.scss';
import '../stylesheets/app.scss';

ReactDom.render((
    <Provider store={store}>
        <MainComponent/>
    </Provider>
    ),document.getElementById("app"));