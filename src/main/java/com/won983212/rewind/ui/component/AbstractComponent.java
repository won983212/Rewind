package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.*;
import net.minecraft.client.gui.components.events.GuiEventListener;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractComponent implements GuiEventListener {
    protected ComponentArea area = new ComponentArea();
    private String id;
    protected AbstractComponent parent;

    private HorizontalArrange hArrange = HorizontalArrange.STRETCH;
    private VerticalArrange vArrange = VerticalArrange.STRETCH;
    private ComponentVec2 preferredMinimumSize = new ComponentVec2(10, 10);
    private ComponentVec2 measuredMinimumSize = null;
    private Thickness margin = new Thickness();
    private Thickness padding = new Thickness();
    private boolean isEnabled = true;
    private boolean isVisible = true;

    public Object layoutData = null;


    protected abstract void renderComponent(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (!isVisible) {
            return;
        }

        if (measuredMinimumSize == null) {
            getActualMinimumSize();
            layout();
        }

        float x = getX();
        float y = getY();

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        renderComponent(poseStack, (int) (mouseX - x), (int) (mouseY - y), partialTicks);
        poseStack.popPose();
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return area.x;
    }

    public float getY() {
        return area.y;
    }

    public float getWidth() {
        return area.width;
    }

    public float getHeight() {
        return area.height;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * <code>invalidateSize()</code>로 캐시된 값을 삭제할 수 있습니다. 다음 rendering tick 또는
     * <code>getActualMinimumSize()</code>를 호출할 때, 최소 사이즈가 다시 계산되고 <code>layout</code>이 수행됩니다.
     */
    public void invalidateSize() {
        measuredMinimumSize = null;
    }

    public ComponentVec2 getMinSizeWithMargin() {
        return margin.toExpandedSize(getActualMinimumSize());
    }

    /**
     * 모든 연관된 컴포넌트들을 다시 layout합니다.
     * A 컴포넌트에서 <code>B = A.parent.parent ...</code>과 같이 B 컴포넌트 객체를 얻을 수 있으면,
     * A와 B는 연관되었다고 봅니다.
     */
    public void layout() {
        if (parent != null) {
            parent.layout();
        }
    }

    /**
     * 이 컴포넌트의 최소 크기를 계산합니다. 한번 계산하고나면 그 값이 캐시됩니다.
     */
    public ComponentVec2 getActualMinimumSize() {
        if (measuredMinimumSize == null) {
            measuredMinimumSize = measureMinSize();
        }
        return measuredMinimumSize;
    }

    /**
     * 컨텐츠를 rendering할 때 필요한 offset을 계산합니다. padding이나 border thickness를 합하여 반환합니다.
     */
    protected Thickness getPositionOffset() {
        return padding;
    }

    /**
     * 이 컴포넌트의 최소 size를 계산합니다. 여기서 계산되는 최소 size는 margin을 포함하지 않습니다.
     * 계산이 다소 느릴 수 있으므로 (특히 Panel종류에서) 매번 직접 계산하지 말고 <code>getActualMinimumSize</code>를
     * 이용하세요.
     */
    protected ComponentVec2 measureMinSize() {
        return getPositionOffset().toExpandedSize(preferredMinimumSize);
    }

    /**
     * <code>available</code>영역에 컴포넌트를 배치합니다. 배치 방식은 <code>hArrange, vArrange</code>에 따릅니다.
     */
    public void arrange(ComponentArea available) {
        setSizeByArrange(available);
        setPositionByArrange(available);
        arrangeChildren(getPositionOffset().toContentRect(new ComponentArea(0, 0, getWidth(), getHeight())));
    }

    /**
     * <code>available</code>영역에 자식 컴포넌트를 배치합니다. 패널 구현에서 사용됩니다.
     */
    protected void arrangeChildren(ComponentArea available) {
    }

    private void setPositionByArrange(ComponentArea available) {
        ComponentArea contentArea = margin.toContentRect(available);
        area.x = hArrange.getArrangedX(contentArea, area.width);
        area.y = vArrange.getArrangedY(contentArea, area.height);
    }

    private void setSizeByArrange(ComponentArea available) {
        ComponentVec2 size = getActualMinimumSize();
        ComponentArea contentArea = margin.toContentRect(available);
        area.width = Math.min(Math.max(preferredMinimumSize.x, hArrange.getArrangedWidth(contentArea, size.x)), contentArea.width);
        area.height = Math.min(Math.max(preferredMinimumSize.y, vArrange.getArrangedHeight(contentArea, size.y)), contentArea.height);
    }

    public AbstractComponent setParent(AbstractComponent parent) {
        this.parent = parent;
        return this;
    }

    public AbstractComponent setId(String id) {
        this.id = id;
        return this;
    }

    public AbstractComponent setX(float x) {
        this.area.x = x;
        return this;
    }

    public AbstractComponent setY(float y) {
        this.area.y = y;
        return this;
    }

    public AbstractComponent setPosition(float x, float y) {
        setX(x);
        setY(y);
        return this;
    }

    public AbstractComponent setVisible(boolean visible) {
        this.isVisible = visible;
        return this;
    }

    public AbstractComponent setEnabled(boolean enable) {
        this.isEnabled = enable;
        return this;
    }

    public AbstractComponent setHorizontalArrange(HorizontalArrange arr) {
        this.hArrange = arr;
        invalidateSize();
        return this;
    }

    public AbstractComponent setVerticalArrange(VerticalArrange arr) {
        this.vArrange = arr;
        invalidateSize();
        return this;
    }

    public AbstractComponent setMargin(Thickness margin) {
        this.margin = margin;
        invalidateSize();
        return this;
    }

    public AbstractComponent setPadding(Thickness padding) {
        this.padding = padding;
        invalidateSize();
        return this;
    }

    public AbstractComponent setPreferredMinimalSize(float width, float height) {
        this.preferredMinimumSize = new ComponentVec2(width, height);
        invalidateSize();
        return this;
    }

    public AbstractComponent setArrange(Arrange arrange) {
        setHorizontalArrange(arrange.horizontal);
        setVerticalArrange(arrange.vertical);
        return this;
    }

    protected ComponentVec2 getGlobalPosition() {
        ComponentVec2 result = new ComponentVec2(area.x, area.y);
        AbstractComponent comp = parent;
        while (comp != null) {
            result.x += comp.getX();
            result.y += comp.getY();
            comp = comp.parent;
        }
        return result;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!isEnabled || !isVisible) {
            return false;
        }
        ComponentVec2 position = getGlobalPosition();
        return mouseX >= position.x && mouseX <= position.x + area.width &&
                mouseY >= position.y && mouseY <= position.y + area.height;
    }
}
