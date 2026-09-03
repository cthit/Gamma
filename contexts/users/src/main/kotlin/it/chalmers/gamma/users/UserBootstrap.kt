package it.chalmers.gamma.users

enum class AdministratorBootstrapResult {
    ALREADY_CONFIGURED,
    ADMIN_CID_IN_USE,
    PASSWORD_REQUIRED,
    CREATED,
}

class UserBootstrap(
    private val users: UserStore,
) {
    fun ensureAdministrator(password: PlainTextPassword?): AdministratorBootstrapResult {
        users.existingConfiguration()?.let { return it }
        if (password == null) return AdministratorBootstrapResult.PASSWORD_REQUIRED

        val registration =
            users.prepareRegistration(
                NewUser(
                    cid = Cid("admin"),
                    nick = Nick("admin"),
                    firstName = FirstName("admin"),
                    lastName = LastName("admin"),
                    acceptanceYear = AcceptanceYear.of(2018, currentYear = 2018),
                    language = Language.EN,
                    email = Email("admin@chalmers.it"),
                    password = password,
                ),
            )
        return users.createAdministrator(registration)
    }
}
