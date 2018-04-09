package com.ecandle.raykun.models

/**
 * Created by juantomaylla on 15/01/17.
 */
/**
"staffid": "1",
"email": "juan.tomaylla@gmail.com",
"firstname": "Juan",
"lastname": "Tomaylla",
"facebook": "",
"linkedin": "",
"phonenumber": "+51999431747",
"skype": "",
"password": "$2a$08$Rayt1uAOd1nUU5I1buXJT.dK7qYlwogx2iV/iUiBwbB8oERIw8ZN.",
"datecreated": "2017-12-13 02:09:17",
"profile_image": null,
"last_ip": "190.239.85.38",
"last_login": "2018-01-28 10:52:23",
"last_activity": "2018-01-28 10:52:26",
"last_password_change": null,
"new_pass_key": null,
"new_pass_key_requested": null,
"admin": "1",
"role": "0",
"active": "1",
"default_language": "spanish",
"direction": "",
"media_path_slug": null,
"is_not_staff": "0",
"hourly_rate": "0.00",
"two_factor_auth_enabled": "0",
"two_factor_auth_code": null,
"two_factor_auth_code_requested": null,
"email_signature": ""
 **/
class DataLogin {

    var staffid: String
    var email: String
    var firstname: String
    var lastname: String
    var phonenumber: String
    var datecreated: String
    var profile_image: String
    var admin: String
    var role: String
    var active: String
    var is_not_staff: String

    constructor(staffid: String, email: String, firstname: String, lastname: String, phonenumber: String,
                datecreated: String, profile_image: String,admin: String, role: String,active: String,is_not_staff: String) {

        this.staffid = staffid
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.phonenumber = phonenumber
        this.datecreated = datecreated
        this.profile_image = profile_image
        this.admin = admin
        this.role = role
        this.active = active
        this.is_not_staff = is_not_staff

    }

}
