const path = require("path");
const webpack = require("webpack");

var config = {
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

module.exports = (env, argv) => {
    if (argv.mode === 'production') {
        const TerserPlugin = require('terser-webpack-plugin');
        const JavaScriptObfuscator = require('webpack-obfuscator');

        config.optimization = {
            minimize: true,
            minimizer: [
                new TerserPlugin({
                    parallel: true,
                    terserOptions: {
                        ecma: 8
                    }
                })
            ]
        };
        config.plugins.push(
            new JavaScriptObfuscator({
                identifierNamesGenerator: 'hexadecimal',
                stringArray: true,
                stringArrayEncoding: 'rc4',
                stringArrayThreshold: 1,
                transformObjectKeys: true,
                selfDefending: true
            }, [])
        );
    }
    return config;
};