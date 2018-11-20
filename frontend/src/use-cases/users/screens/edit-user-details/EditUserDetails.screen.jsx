import React from "react";
import {
    GammaCardDisplayTitle,
    GammaCardBody,
    GammaCard
} from "../../../../common-ui/design";

import GammaTextField from "../../../../common/elements/gamma-text-field";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Center } from "../../../../common-ui/layout";
import UserForm from "../common-views/user-form/UserForm.view";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./EditUserDetails.screen.translations.json";

class EditUserDetails extends React.Component {
    componentDidMount() {
        this.props.websitesLoad();
    }

    render() {
        const { user, usersChange, websites } = this.props;
        return (
            <IfElseRendering
                test={user != null && websites != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Users.Screen.EditUserDetails"
                        render={text => (
                            <Center>
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
                                        usersChange(values, user.cid).then(
                                            response => {
                                                console.log("Save");
                                            }
                                        );
                                    }}
                                />
                            </Center>
                        )}
                    />
                )}
            />
        );
    }
}
export default EditUserDetails;
