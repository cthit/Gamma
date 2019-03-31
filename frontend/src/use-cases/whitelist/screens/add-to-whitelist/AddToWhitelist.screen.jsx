import React, { Component } from "react";
import * as yup from "yup";
import {
    DigitTranslations,
    DigitLayout,
    DigitDesign,
    DigitForm,
    DigitButton
} from "@cthit/react-digit-components";
import translations from "./AddToWhitelist.screen.translations.json";
import WhitelistItemArray from "./elements/whitelist-item-array";

class AddNewWhitelistItem extends Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const { addToWhitelist, redirectTo, toastOpen } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Whitelist.Screen.AddNewWhitelistItem"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitLayout.Center>
                            <DigitForm
                                onSubmit={(values, actions) => {
                                    addToWhitelist(values)
                                        .then(response => {
                                            toastOpen({
                                                text: text.SuccessfullAdd
                                            });
                                            redirectTo("/whitelist");
                                            actions.resetForm();
                                        })
                                        .catch(error => {
                                            toastOpen({
                                                text: text.SomethingWentWrong,
                                                duration: 6000
                                            });
                                        });
                                }}
                                validationSchema={yup.object().shape({
                                    cids: yup
                                        .array()
                                        .of(yup.string())
                                        .required()
                                })}
                                initialValues={{ cids: [] }}
                                render={() => (
                                    <DigitLayout.Column>
                                        <DigitDesign.Card>
                                            <DigitDesign.CardTitle text="Add whitelist items" />
                                            <DigitDesign.CardBody>
                                                <WhitelistItemArray />
                                            </DigitDesign.CardBody>
                                            <DigitDesign.CardButtons
                                                reverseDirection
                                            >
                                                <DigitButton
                                                    primary
                                                    raised
                                                    submit
                                                    text="Save whitelist"
                                                />
                                                <DigitDesign.Link to="/whitelist">
                                                    <DigitButton text="Cancel" />
                                                </DigitDesign.Link>
                                            </DigitDesign.CardButtons>
                                        </DigitDesign.Card>
                                    </DigitLayout.Column>
                                )}
                            />
                        </DigitLayout.Center>
                    </DigitLayout.Fill>
                )}
            />
        );
    }
}

export default AddNewWhitelistItem;
