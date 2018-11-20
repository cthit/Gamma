import { DigitButton, DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaLink
} from "../../../../common-ui/design";
import { Center, Fill, Spacing } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowWhitelistItem.screen.translations.json";

const ShowWhitelistItem = ({
    whitelistItem,
    whitelistDelete,
    redirectTo,
    toastOpen,
    gammaDialogOpen
}) => (
    <IfElseRendering
        test={whitelistItem != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Whitelist.Screen.ShowWhitelistItem"
                render={text => (
                    <Fill>
                        <Center>
                            <GammaCard minWidth="300px" maxWidth="600px">
                                <GammaCardBody>
                                    <DigitDisplayData
                                        data={whitelistItem}
                                        keysOrder={["id", "cid"]}
                                        keysText={{
                                            id: text.Id,
                                            cid: text.Cid
                                        }}
                                    />
                                </GammaCardBody>
                                <GammaCardButtons reversedDirection>
                                    <DigitButton
                                        text={text.DeleteWhitelistItem}
                                        onClick={() => {
                                            gammaDialogOpen({
                                                title:
                                                    text.WouldYouLikeToDelete +
                                                    " " +
                                                    whitelistItem.cid +
                                                    "?",
                                                confirmButtonText:
                                                    text.DeleteWhitelistItem,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    whitelistDelete(
                                                        whitelistItem.id
                                                    )
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    text.DeleteSuccessfully +
                                                                    " " +
                                                                    whitelistItem.cid
                                                            });
                                                            redirectTo(
                                                                "/whitelist"
                                                            );
                                                        })
                                                        .catch(error => {
                                                            toastOpen({
                                                                text:
                                                                    text.SomethingWentWrong
                                                            });
                                                        });
                                                }
                                            });
                                        }}
                                    />
                                    <Spacing />
                                    <GammaLink
                                        to={
                                            "/whitelist/" +
                                            whitelistItem.id +
                                            "/edit"
                                        }
                                    >
                                        <DigitButton
                                            text={text.EditWhitelistItem}
                                            primary
                                            raised
                                        />
                                    </GammaLink>
                                </GammaCardButtons>
                            </GammaCard>
                        </Center>
                    </Fill>
                )}
            />
        )}
    />
);

export default ShowWhitelistItem;
