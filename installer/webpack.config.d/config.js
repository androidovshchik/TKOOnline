const path = require('path');

config.output.path = path.join(__dirname, '/../tkonline/app/assets');
config.output.filename = 'app.min.js';

if (defined.PRODUCTION) {
    const TerserPlugin = require('./node_modules/terser-webpack-plugin');

    config.optimization = {
        minimize: true,
        minimizer: [
            new TerserPlugin({
                parallel: true,
                terserOptions: {
                    ecma: 6
                }
            })
        ]
    };
}