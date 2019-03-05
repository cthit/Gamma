import React, { Component } from "react";

import {
    DigitTranslations,
    DigitIfElseRendering,
    DigitLayout
} from "@cthit/react-digit-components";

import translations from "./EditSuperGroupDetails.screen.translations";
import SuperGroupForm from "../common-views/super-group-form";

class EditSuperGroup extends Component {
    constructor(props) {
        super();

        props.getSuperGroup(props.superGroupId).then(() => {
            props.gammaLoadingFinished();
        });
    }

    render() {
        const { superGroup, editSuperGroup, superGroupId } = this.props;

        return (
            <DigitIfElseRendering
                test={superGroup != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Center>
                                <SuperGroupForm
                                    onSubmit={values => {
                                        editSuperGroup(superGroupId, values);
                                    }}
                                    backButtonTo="/super-groups"
                                    initialValues={superGroup}
                                    submitText={text.Save}
                                    titleText={text.EditSuperGroup}
                                />
                            </DigitLayout.Center>
                        )}
                    />
                )}
            />
        );
    }
}

export default EditSuperGroup;
