import React from "react";
import {
    DigitIfElseRendering,
    DigitTranslations,
    DigitLayout,
    DigitFAB
} from "@cthit/react-digit-components";
import translations from "./MyAccount.view.translations";
import DisplayUserDetails from "../../../../common/elements/display-user-details/DisplayUserDetails.element";
import Delete from "@material-ui/icons/Delete";

class MyAccount extends React.Component {
    componentDidMount() {
        this.props.deltaLoadingFinished();
    }

    render() {
        const { me, redirectTo } = this.props;

        return (
            <DigitIfElseRendering
                test={me != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <div>
                                <DigitLayout.Center>
                                    <DisplayUserDetails user={me} isMe={true} />
                                </DigitLayout.Center>
                                <DigitLayout.DownRightPosition>
                                    <DigitFAB
                                        onClick={() => {
                                            redirectTo("/me/delete");
                                        }}
                                        icon={Delete}
                                        text={text.Delete}
                                        secondary
                                    />
                                </DigitLayout.DownRightPosition>
                            </div>
                        )}
                    />
                )}
            />
        );
    }
}

export default MyAccount;
