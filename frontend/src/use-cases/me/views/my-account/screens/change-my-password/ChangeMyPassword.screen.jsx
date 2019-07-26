import React from "react";
import translations from "./ChangeMyPassword.screen.translations";
import ChangeUserPasswordForm from "../../../../../../common/elements/change-user-password";
import {
    DigitIfElseRendering,
    DigitTranslations
} from "@cthit/react-digit-components";

class ChangeMyPassword extends React.Component {
    componentDidMount() {
        const { gammaLoadingFinished } = this.props;
        gammaLoadingFinished();
    }

    render() {
        const { me, changePassword, toastOpen, redirectTo } = this.props;
        return (
            <DigitIfElseRendering
                test={me != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <ChangeUserPasswordForm
                                renderOldPassword={true}
                                user={me.cid}
                                onSubmit={(values, actions) => {
                                    changePassword(values).then(response => {
                                        toastOpen({
                                            text:
                                                text.ChangePasswordSuccessfully +
                                                ' "' +
                                                me.cid +
                                                '"',
                                            duration: 5000
                                        });
                                        redirectTo("/me");
                                    });
                                }}
                            />
                        )}
                    />
                )}
            />
        );
    }
}

export default ChangeMyPassword;
