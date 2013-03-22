CryptoApplet = {};

CryptoApplet.API = {

    protocol: 'http',
    host: 'localhost',
    port: 12345,

    _getBaseUrl : function () {
        return this.protocol + '://' + this.host + ':' + this.port + '/services';
    },

    _callSignatureService : function (url, params, callback, errorCallback) {
        $.getJSON(url, params || {}, callback).error(errorCallback || function (e) {
            console.log(e);
        });
    },

    getCertificates : function (callback) {
        var baseUrl = this._getBaseUrl();
        this._callSignatureService(baseUrl + '/certificates?callback=?', {}, callback);
    },

    signReference : function(serial, dn, inputUrl, callback) {
        var baseUrl = this._getBaseUrl();
        this._callSignatureService(baseUrl + '/signraw?callback=?', {
            serial : serial,
            dn : dn,
            inputUrl : inputUrl
        }, callback);
    }
}

CryptoApplet.API.SignatureOptions = function()
{    
}

CryptoApplet.API.SignatureOptions.prototype.setCertificate = function(serial, dn)
{
    this.serial = serial;
    this.dn = dn;
}

$(document).ready(function() {
    CryptoApplet.API.getCertificates(function(data) {
        var output = "";

        for (var i = 0, len = data.certificate.length ; i < len ; i++) {
            var certificate = data.certificate[i];

            output+= '<li>';
            output+= '  <a href="javascript:void(0)" class="certificate" data-serial="' + certificate.serial + '" data-dn="' + certificate.dn + '">';
            output+= certificate.dn;
            output+= '  </a>';
            output+= '</li>';
        }

        $('#certificateList').html("<ul>" + output + "</ul>");

        $('.certificate').click(function(link) {
            var serial = $(this).attr('data-serial');
            var dn = $(this).attr('data-dn');
            var inputUrl = $('#inputUrl').val();

            CryptoApplet.API.signReference(serial, dn, inputUrl, function(data) {
                console.log(data);
                $('#signatureResult').val(data.signature);
            });
        });
    });
});