import React from "react";
import UserForm from "../common-views/user-form";
import translations from "./CreateNewUser.screen.translations";
import { DigitTranslations, DigitLayout } from "@cthit/react-digit-components";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    LAST_NAME,
    NICK,
    PASSWORD,
    USER_AGREEMENT,
    WEBSITES
} from "../../../../api/users/props.users.api";

class CreateNewUser extends React.Component {
    componentDidMount() {
        const { websitesLoad, gammaLoadingFinished } = this.props;

        websitesLoad().then(() => {
            gammaLoadingFinished();
        });
    }

    generateInitialValues = () => {
        const output = {};

        output[FIRST_NAME] = "";
        output[LAST_NAME] = "";
        output[CID] = "";
        output[NICK] = "";
        output[PASSWORD] = "";
        output[EMAIL] = "";
        output[WEBSITES] = [];

        return output;
    };

    render() {
        const { websites, addUser } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                render={text => (
                    <DigitLayout.Center>
                        <UserForm
                            includeCidAndPassword
                            initialValues={this.generateInitialValues()}
                            availableWebsites={websites}
                            titleText={text.CreateUser}
                            submitText={text.Create}
                            onSubmit={(values, actions) => {
                                const output = { ...values };
                                output[USER_AGREEMENT] = true;
                                delete output[WEBSITES];
                                output[ACCEPTANCE_YEAR] = parseInt(
                                    values[ACCEPTANCE_YEAR]
                                );
                                addUser(output);
                                // actions.resetForm();
                            }}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}

export default CreateNewUser;
