import React from "react";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";

import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaLink,
  GammaCardButtons
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaButton from "../../../../common/elements/gamma-button";
import GammaDisplayData from "../../../../common/elements/gamma-display-data/GammaDisplayData.element";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowPostDetails.screen.translations.json";

function getPostName(post, activeLanguage) {
  switch (activeLanguage.code.toLowerCase()) {
    case "en":
      return post.en;
    case "sv":
      return post.sv;
    default:
      post.en + "/" + post.sv;
  }
}

const ShowPostDetails = ({
  post,
  gammaDialogOpen,
  toastOpen,
  redirectTo,
  postsDelete
}) => (
  <IfElseRendering
    test={post != null}
    ifRender={() => (
      <GammaTranslations
        translations={translations}
        uniquePath="Posts.Screen.ShowPostDetails"
        render={(text, activeLanguage) => (
          <Fill>
            <Center>
              <GammaCard minWidth="300px" maxWidth="600px">
                <GammaCardBody>
                  <GammaDisplayData
                    data={post}
                    keysText={{ id: text.Id, sv: text.Sv, en: text.En }}
                    keysOrder={["id", "sv", "en"]}
                  />
                </GammaCardBody>
                <GammaCardButtons reverseDirection>
                  <GammaLink to={"/posts/" + post.id + "/edit"}>
                    <GammaButton text="Redigera" primary raised />
                  </GammaLink>
                  <Spacing />
                  <GammaButton
                    text={text.DeletePost}
                    onClick={() => {
                      gammaDialogOpen({
                        title:
                          text.WouldYouLikeToDelete +
                          " " +
                          getPostName(post, activeLanguage),
                        confirmButtonText: text.DeletePost,
                        cancelButtonText: text.Cancel,
                        onConfirm: () => {
                          postsDelete(post.id)
                            .then(response => {
                              toastOpen({
                                text:
                                  text.YouHaveDeleted +
                                  " " +
                                  getPostName(post, activeLanguage)
                              });
                              redirectTo("/posts");
                            })
                            .catch(error => {
                              toastOpen({
                                text: text.SomethingWentWrong
                              });
                            });
                        }
                      });
                    }}
                  />
                </GammaCardButtons>
              </GammaCard>
            </Center>
          </Fill>
        )}
      />
    )}
  />
);

export default ShowPostDetails;
