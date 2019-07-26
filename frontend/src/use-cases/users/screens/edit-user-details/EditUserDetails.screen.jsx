import {
    DigitLayout,
    DigitTranslations,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import UserForm from "../../../../common/elements/user-form/UserForm.view";
import translations from "./EditUserDetails.screen.translations.json";

class EditUserDetails extends React.Component {
    componentDidMount() {
        const {
            getUser,
            websitesLoad,
            gammaLoadingFinished,
            userId
        } = this.props;

        Promise.all([getUser(userId), websitesLoad()]).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const {
            user,
            usersChange,
            websites,
            toastOpen,
            redirectTo
        } = this.props;
        return (
            <DigitIfElseRendering
                test={user != null && websites != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Center>
                                <UserForm
                                    availableWebsites={websites}
                                    titleText={text.EditUser}
                                    submitText={text.SaveUser}
                                    initialValues={{
                                        ...user,
                                        websites:
                                            user.websites == null
                                                ? []
                                                : user.websites,
                                        acceptanceYear: user.acceptanceYear + ""
                                    }}
                                    onSubmit={(values, actions) => {
                                        usersChange(values, user.id).then(
                                            response => {
                                                toastOpen({
                                                    text:
                                                        text.UserUpdated +
                                                        user.cid
                                                });
                                                redirectTo("/users/" + user.id);
                                            }
                                        );
                                    }}
                                />
                            </DigitLayout.Center>
                        )}
                    />
                )}
            />
        );
    }
}

export default EditUserDetails;
