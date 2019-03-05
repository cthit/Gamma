import React from "react";
import { DigitLayout, DigitTranslations } from "@cthit/react-digit-components";
import SuperGroupForm from "../common-views/super-group-form";

import translations from "./CreateNewSuperGroup.screen.translations";
import {
    NAME,
    PRETTY_NAME,
    TYPE
} from "../../../../api/super-groups/props.super-groups.api";

function generateInitialValues() {
    const output = {};

    output[NAME] = "";
    output[PRETTY_NAME] = "";
    output[TYPE] = "";

    return output;
}

class CreateNewSuperGroup extends React.Component {
    constructor(props) {
        super(props);

        props.gammaLoadingFinished();
    }

    render() {
        const { addSuperGroup } = this.props;

        return (
            <DigitLayout.Center>
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <SuperGroupForm
                            initialValues={generateInitialValues()}
                            onSubmit={values => {
                                addSuperGroup(values);
                            }}
                            titleText={text.CreateSuperGroup}
                            submitText={text.Create}
                            backButtonTo={"/super-groups"}
                        />
                    )}
                />
            </DigitLayout.Center>
        );
    }
}

export default CreateNewSuperGroup;
