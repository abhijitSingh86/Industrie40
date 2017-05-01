'use strict';

var webpack = require('webpack'),
    jsPath  = 'app/assets/javascripts',
    path = require('path'),
    srcPath = path.join(__dirname, 'app/assets/javascripts');

var config = {
    target: 'web',
    entry: {
        app: path.join(srcPath, 'app.jsx')
        //, common: ['react-dom', 'react']
    },
    resolve: {
        alias: {
            rdg:"react-data-grid/dist/react-data-grid.js"
        },
        // root: srcPath,
        extensions: ['.js'],
        modules: ['node_modules', jsPath]
    },
    output: {
        path:path.resolve(__dirname, jsPath, 'build'),
        publicPath: '',
        filename: '[name].js'
    },

    module: {

        loaders: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                loader: 'babel-loader'
            },
            { test: /\.css$/, loader: 'style-loader!css-loader' },
            {
                test: /\.scss$/,
                include: /\/app\/assets/,
                loader: 'style-loader!css-loader!sass-loader'
            }
        ]
    },
    plugins: [
        //new webpack.optimize.CommonsChunkPlugin('common', 'common.js'),
        new webpack.optimize.UglifyJsPlugin({
            compress: { warnings: false },
            output: { comments: false }
        }),
        new webpack.NoEmitOnErrorsPlugin(),
        new webpack.ProvidePlugin({
            jQuery: 'jquery',
            $: 'jquery',
            jquery: 'jquery'
        })
    ]
};

module.exports = config;