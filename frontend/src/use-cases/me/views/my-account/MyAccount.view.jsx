import React from "react";
import {
    DigitIfElseRendering,
    DigitDesign,
    DigitText,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";
import translations from "./MyAccount.view.translations";
import DisplayUserDetails from "../../../../common/elements/display-user-details/DisplayUserDetails.element";

class MyAccount extends React.Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const { me } = this.props;

        return (
            <DigitIfElseRendering
                test={me != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Center>
                                <DisplayUserDetails user={me} />
                            </DigitLayout.Center>
                        )}
                    />
                )}
            />
        );
    }
}

export default MyAccount;
