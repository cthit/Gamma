import React from "react";
import translations from "./ChangeUserPassword.screen.translations";
import ChangeUserPasswordForm from "../../../../common/elements/change-user-password";
import {
    DigitIfElseRendering
} from "@cthit/react-digit-components";

class ChangeUserPassword extends React.Component {
    componentDidMount() {
        const {
            getUser,
            userId,
            gammaLoadingFinished
        } = this.props;
        getUser(userId).then(() => {
            gammaLoadingFinished();
        });
    }

    render() {
        const {
            user,
            changePassword,
            redirectTo,
            toastOpen,
        } = this.props;
        return (
            <DigitIfElseRendering
                test={user != null}
                ifRender={() => (
                    <ChangeUserPasswordForm
                        renderOldPassword={false}
                        user={user.cid}
                        onSubmit={(values, actions) => {
                            changePassword(user.id, values).then(
                                response => {console.log(response)}
                            );
                        }}
                    />
                )}
            />

        )
            ;
    }
}

export default ChangeUserPassword;