import React from "react";
import { Fill, Center } from "../../../../common-ui/layout";
import EditWhitelistItem from "../common-views/edit-whitelist-item";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./EditWhitelistItemDetails.screen.translations.json";

const EditWhitelistItemDetails = ({
  whitelistChange,
  whitelistItem,
  match
}) => (
  <IfElseRendering
    test={whitelistItem != null}
    ifRender={() => (
      <GammaTranslations
        translations={translations}
        uniquePath="Whitelist.Screen.EditWhitelistItemDetails"
        render={text => (
          <Fill>
            <Center>
              <EditWhitelistItem
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
