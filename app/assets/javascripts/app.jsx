import React from 'react';
import ReactDom from 'react-dom';
import MainComponent from './components/MainComponent';


import '../stylesheets/style.scss';
import '../stylesheets/app.scss';

ReactDom.render((<MainComponent/>),document.getElementById("app"));