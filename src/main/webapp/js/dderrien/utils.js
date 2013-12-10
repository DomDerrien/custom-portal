define(
    [
        'dojo/date/locale'
    ],
    function(dateLocale) {
        // module:
        //   ns/utils
        // summary:
        //   The module defines a set of utility methods

        var _formatDateTime = function(value) {
            if (!value) {
                return '';
            }
            return dateLocale.format(new Date(value), {
                datePattern: 'MMM d, yyy',
                timePattern: 'H:mm z'
            });
        };

        var _formatDate = function(value) {
            if (!value) {
                return '';
            }
            return dateLocale.format(new Date(value), {
                selector: 'date',
                datePattern: 'MMM d, yyyy'
            });
        };

        var _formatTime = function(value) {
            if (!value) {
                return '';
            }
            return dateLocale.format(new Date(value), {
                selector: 'time',
                timePattern: 'H:mm z'
            });
        };

        // Publish these methods plus others at the AMD level (to be accessible to AMD module consumers)
        return {
            formatDateTime: _formatDateTime,
            formatDate: _formatDate,
            formatTime: _formatTime
        };
    }
);
