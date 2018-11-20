import React from "react";
import { Fill, Center } from "../../../../common-ui/layout";
import WhitelistItemForm from "../common-views/whitelist-item-form";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./EditWhitelistItemDetails.screen.translations.json";

const EditWhitelistItemDetails = ({
    whitelistChange,
    whitelistItem,
    match
}) => (
    <IfElseRendering
        test={whitelistItem != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Whitelist.Screen.EditWhitelistItemDetails"
                render={text => (
                    <Fill>
                        <Center>
                            <WhitelistItemForm
                                onSubmit={(values, actions) => {
                                    whitelistChange(values, match.params.id)
                                        .then(response => {
                                            console.log(response);
                                            actions.resetForm();
                                        })
                                        .catch(error => {
                                            console.log(error);
                                        });
                                }}
                                initialValues={{ cid: whitelistItem.cid }}
                                titleText={text.EditCid}
                                cidInputText={text.Cid}
                                fieldRequiredText={text.FieldRequired}
                                submitText={text.SaveCid}
                            />
                        </Center>
                    </Fill>
                )}
            />
        )}
    />
);

export default EditWhitelistItemDetails;
