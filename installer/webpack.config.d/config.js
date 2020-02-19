const path = require('path');

config.output.path = path.join(__dirname, '/../tkonline/app/assets');
config.output.filename = 'app.min.js';

const concat = require('concat');

concat([
    'node_modules/bootstrap/dist/css/bootstrap.min.css'
], path.join(__dirname, '/../tkonline/app/assets/vendor.min.css'));
concat([
    'node_modules/jquery/dist/jquery.slim.min.js',
    'node_modules/bootstrap/dist/js/bootstrap.bundle.min.js',
    'node_modules/bootbox/dist/bootbox.all.min.js'
], path.join(__dirname, '/../tkonline/app/assets/vendor.min.js'));

if (defined.PRODUCTION) {
    const TerserPlugin = require('terser-webpack-plugin');

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