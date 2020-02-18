// noinspection JSUnresolvedVariable
if (defined.PRODUCTION) {
    // noinspection JSFileReferences
    const TerserPlugin = require('./node_modules/terser-webpack-plugin');

    // noinspection JSUnresolvedVariable
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