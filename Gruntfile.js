module.exports = function(grunt) {
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-stylus');
	
	grunt.initConfig({
		jshint: {
			files: [
				'Gruntfile.js',
				'src/main/webapp/js/*.js',
				'src/test/js/*.js'
			]
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
					'src/main/webapp/portal.css': [
						'src/main/webapp/styles/*.styl'
					]
				}
			}
		}
	});

	grunt.registerTask('test', [ 'stylus' ]);
	grunt.registerTask('default', ['jshint', 'stylus']);
};
