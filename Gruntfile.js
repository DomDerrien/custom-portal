module.exports = function(grunt) {
    grunt.loadNpmTasks('grunt-jsbeautifier');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-stylus');

    grunt.initConfig({
        jsbeautifier: {
            sources: [
                'Gruntfile.js',
                'src/main/webapp/js/dderrien/**/*.js',
                'src/test/js/**/*.js'
            ],
            options: { // https://github.com/einars/js-beautify#options
                'indent_size': 4,
                'indent_char': ' ',
                'indent_level': 0,
                'indent_with_tabs': false,
                'preserve_newlines': true,
                'max_preserve_newlines': 2,
                'jslint_happy': true,
                'brace_style': 'collapse',
                'keep_array_indentation': false,
                'keep_function_indentation': false,
                'space-in-parenthesis': false,
                'space_before_conditional': true,
                'break_chained_methods': false,
                'eval_code': false,
                'unescape_strings': false,
                'wrap_line_length': 0
            }
        },
        jshint: {
            files: [
                'Gruntfile.js',
                'src/main/webapp/js/dderrien/**/*.js',
                'src/test/js/**/*.js'
            ],
            options: { // http://www.jshint.com/docs/options/
                'globals': {
                    'module': true,
                    'define': true,
                    'require': true,
                    'console': true
                },
                'browser': true,
                'bitwise': true,
                'curly': true,
                'dojo': true,
                'eqeqeq': true,
                'immed': true,
                'indent': 4,
                'latedef': true,
                'newcap': true,
                'noempty': true,
                'nonew': true,
                'trailing': true,
                'undef': true,
                'unused': true
            }
        },
        stylus: {
            compile: {
                options: {
                    banner: '', // Text prepended to the compiled CSS
                    compress: true,
                    define: { // Global variables available in Stylus files
                    },
                    firebug: false, // For debugging by the FireStylus plugin
                    import: [ // List of Stylus files always automatically imported
                    ],
                    'include css': true, // To force the inclusion of external CSS, to form a unique file
                    linenos: false, // For debugging
                    paths: [ // List of directories scanned while processing @import
                        'src/main/webapp/styles'
                    ],
                    'resolve url': true, // To force the generation of relative URLs when the imported Stylus file is in a subfolder
                    urlfunc: 'embedurl', // Name of function converting images in data URI
                    use: [ // List of Stylus plugins
                    ]
                },
                files: {
                    'src/main/webapp/styles/portal.css': [
                        'src/main/webapp/styles/*.styl'
                    ]
                }
            }
        }
    });

    grunt.registerTask('process-sources', ['jsbeautifier', 'jshint']);
    grunt.registerTask('prepare-package', ['stylus']);
    grunt.registerTask('default', ['jshint']);
};
