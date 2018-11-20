import { DigitTextField, DigitTranslations, DigitIconButton } from "@cthit/react-digit-components";
import { Cancel, Delete, Edit, Save } from "@material-ui/icons";
import React from "react";
import { Hide, Size, Spacing, VerticalFill } from "../../../../../common-ui/layout";
import { Text } from "../../../../../common-ui/text";
import GammaTooltip from "../../../../elements/gamma-tooltip";
import { CustomVerticalFill, CustomVerticalFillRightAlign } from "./EditableWebsite.view.styles";
import translations from "./EditableWebsite.view.translations.json";
    };
    render() {
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Common.Views.EditWebsites.SubView.EditableWebsite"
                render={text => (
                    <CustomVerticalFill>
                        <VerticalFill>
                            <Size minWidth="150px">
                                <Text text={prettyWebsite} />
                            </Size>
                            <Spacing />
                            <Hide hidden={editing}>
                                <Text text={url} />
                            </Hide>
                            <Hide hidden={!editing}>
                                <DigitTextField
                                    upperLabel={text.Url}
                                    value={newUrl}
                                    onChange={e => {
                                        this.setState({
                                            newUrl: e.target.value
                                        });
                                    }}
                                />
                            </Hide>
                        </VerticalFill>
                        <CustomVerticalFillRightAlign>
                            <Hide hidden={editing}>
                                <GammaTooltip text={text.Edit}>
                                    <DigitIconButton
                                        component={Edit}
                                        onClick={() =>
                                            this.setState({
                                                editing: true
                                            })
                                        }
                                    />
                                </GammaTooltip>
                            </Hide>
                            <Hide hidden={!editing}>
                                <GammaTooltip text={text.Cancel}>
                                    <DigitIconButton
                                        component={Cancel}
                                        onClick={() =>
                                            this.setState({
                                                editing: false,
                                                newUrl: url
                                            })
                                        }
                                    />
                                </GammaTooltip>
                                <GammaTooltip text={text.Save}>
                                    <DigitIconButton
                                        component={Save}
                                        onClick={() => {
                                            this.setState({
                                                editing: false
                                            });
                                            onChange({
                                                website: website,
                                                url: this.state.newUrl
                                            });
                                        }}
                                    />
                                </GammaTooltip>
                            </Hide>
                            <div>
                                <GammaTooltip text={text.Delete}>
                                    <DigitIconButton
                                        component={Delete}
                                        onClick={() => onDelete()}
                                    />
                                </GammaTooltip>
                            </div>
                        </CustomVerticalFillRightAlign>
                    </CustomVerticalFill>
                )}
            />
        );
    }
}

export default EditableWebsite;
