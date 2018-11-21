import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout
} from "@cthit/react-digit-components";
import React from "react";
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
                    <DigitLayout.Fill>
                        <DigitLayout.Center>
                            <DigitDesign.Card minWidth="300px" maxWidth="600px">
                                <DigitDesign.CardBody>
                                    <DigitDisplayData
                                        data={whitelistItem}
                                        keysOrder={["id", "cid"]}
                                        keysText={{
                                            id: text.Id,
                                            cid: text.Cid
                                        }}
                                    />
                                </DigitDesign.CardBody>
                                <DigitDesign.CardButtons reversedDirection>
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
                                    <DigitLayout.Spacing />
                                    <DigitDesign.Link
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
                                    </DigitDesign.Link>
                                </DigitDesign.CardButtons>
                            </DigitDesign.Card>
                        </DigitLayout.Center>
                    </DigitLayout.Fill>
                )}
            />
        )}
    />
);

export default ShowWhitelistItem;
