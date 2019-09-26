const path = require("path");
const webpack = require("webpack");

module.exports = {
    devServer: {
        contentBase: path.resolve(__dirname, "../app/src/main/assets")
    },
    entry: [
        "./src/main.ts"
    ],
    output: {
        path: path.resolve(__dirname, "../app/src/main/assets"),
        filename: "map.js"
    },
    module: {
        rules: [{
            test: /\.ts$/,
            use: 'ts-loader',
            exclude: /node_modules/
        }]
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    plugins: []
};