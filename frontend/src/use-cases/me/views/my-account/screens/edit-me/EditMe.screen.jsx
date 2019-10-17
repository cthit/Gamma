import {
    DigitLayout,
    DigitTranslations,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import UserForm from "../../../../../../common/elements/user-form";
import translations from "./EditMe.screen.translations.json";

class EditMe extends React.Component {
    componentDidMount() {
        const { websitesLoad, deltaLoadingFinished } = this.props;

        websitesLoad().then(() => {
            deltaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.deltaLoadingStart();
    }

    render() {
        const { me, editMe, websites, toastOpen, redirectTo } = this.props;
        return (
            <DigitIfElseRendering
                test={me.loaded === true}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Center>
                                <UserForm
                                    availableWebsites={websites}
                                    titleText={text.EditMe + me.cid}
                                    submitText={text.SaveMe}
                                    initialValues={{
                                        ...me,
                                        websites:
                                            me.websites == null
                                                ? []
                                                : me.websites,
                                        acceptanceYear: me.acceptanceYear + ""
                                    }}
                                    onSubmit={(values, actions) => {
                                        editMe(values).then(response => {
                                            toastOpen({
                                                text: text.UserUpdated + me.cid
                                            });
                                            redirectTo("/me");
                                        });
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

export default EditMe;
