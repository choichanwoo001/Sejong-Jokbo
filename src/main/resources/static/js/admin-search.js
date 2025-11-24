(function (global) {
    if (global.AdminSearch) {
        return;
    }

    const defaultMessage = '검색 결과가 없습니다.';

    function normaliseValue(value, caseSensitive) {
        if (value == null) {
            return '';
        }
        const prepared = typeof value === 'string' ? value.trim() : String(value).trim();
        return caseSensitive ? prepared : prepared.toLowerCase();
    }

    function resolveDatasetValue(element, key) {
        if (!key) {
            return '';
        }
        const raw = element.dataset ? element.dataset[key] : undefined;
        return raw == null ? '' : raw;
    }

    function AdminSearch(options) {
        this.formId = options.formId;
        this.itemSelector = options.itemSelector;
        this.fieldConfigs = (options.fieldConfigs || []).map(function (config) {
            return Object.assign({
                datasetKey: config.name,
                matchType: 'includes',
                caseSensitive: false
            }, config);
        });
        this.visibleDisplayStyle = options.visibleDisplayStyle || '';
        this.hiddenDisplayStyle = 'none';
        this.noResult = options.noResult || null;
        this.resetSelector = options.resetSelector || '[data-search-reset]';
        this.afterFilter = typeof options.afterFilter === 'function' ? options.afterFilter : null;
        this.onReset = typeof options.onReset === 'function' ? options.onReset : null;

        this.form = document.getElementById(this.formId);
        this.items = Array.prototype.slice.call(document.querySelectorAll(this.itemSelector));

        if (!this.form || this.items.length === 0) {
            return;
        }

        this.resetButton = this.form.querySelector(this.resetSelector);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleReset = this.handleReset.bind(this);

        this.form.addEventListener('submit', this.handleSubmit);

        if (this.resetButton) {
            this.resetButton.addEventListener('click', this.handleReset);
        }
    }

    AdminSearch.prototype.collectValues = function () {
        const self = this;
        return this.fieldConfigs.reduce(function (accumulator, field) {
            const input = self.form.querySelector('[name="' + field.name + '"]') ||
                document.getElementById(field.name);

            const rawValue = input ? input.value : '';
            accumulator[field.name] = normaliseValue(rawValue, field.caseSensitive);
            return accumulator;
        }, {});
    };

    AdminSearch.prototype.evaluateItem = function (item, values) {
        return this.fieldConfigs.every(function (field) {
            const searchValue = values[field.name];
            if (!searchValue) {
                return true;
            }

            const itemValueRaw = typeof field.getItemValue === 'function'
                ? field.getItemValue(item)
                : resolveDatasetValue(item, field.datasetKey);

            const itemValue = normaliseValue(itemValueRaw, field.caseSensitive);

            if (field.matchType === 'equals') {
                return itemValue === searchValue;
            }

            if (typeof field.matches === 'function') {
                return field.matches(itemValueRaw, searchValue, item, values);
            }

            return itemValue.indexOf(searchValue) > -1;
        });
    };

    AdminSearch.prototype.showItem = function (item) {
        item.style.display = this.visibleDisplayStyle;
    };

    AdminSearch.prototype.hideItem = function (item) {
        item.style.display = this.hiddenDisplayStyle;
    };

    AdminSearch.prototype.ensureNoResultMessage = function (visibleCount) {
        if (!this.noResult || !this.noResult.targetSelector) {
            return;
        }

        const target = document.querySelector(this.noResult.targetSelector);
        if (!target) {
            return;
        }

        const selector = this.noResult.selector || '.no-results[data-admin-search-message="true"]';
        let message = target.querySelector(selector);

        if (visibleCount === 0) {
            if (!message) {
                message = document.createElement('div');
                message.className = this.noResult.className || 'no-results';
                message.dataset.adminSearchMessage = 'true';
                message.textContent = this.noResult.text || defaultMessage;
                target.appendChild(message);
            }
        } else if (message) {
            message.remove();
        }
    };

    AdminSearch.prototype.removeNoResultMessage = function () {
        if (!this.noResult || !this.noResult.targetSelector) {
            return;
        }

        const target = document.querySelector(this.noResult.targetSelector);
        if (!target) {
            return;
        }

        const selector = this.noResult.selector || '.no-results[data-admin-search-message="true"]';
        const message = target.querySelector(selector);
        if (message) {
            message.remove();
        }
    };

    AdminSearch.prototype.applyFilters = function () {
        const values = this.collectValues();
        let visibleCount = 0;

        this.items.forEach(function (item) {
            if (this.evaluateItem(item, values)) {
                this.showItem(item);
                visibleCount += 1;
            } else {
                this.hideItem(item);
            }
        }, this);

        this.ensureNoResultMessage(visibleCount);

        if (this.afterFilter) {
            this.afterFilter({
                values: values,
                items: this.items,
                visibleCount: visibleCount
            });
        }
    };

    AdminSearch.prototype.handleSubmit = function (event) {
        event.preventDefault();
        this.applyFilters();
    };

    AdminSearch.prototype.handleReset = function () {
        this.form.reset();

        this.items.forEach(function (item) {
            this.showItem(item);
        }, this);

        this.removeNoResultMessage();

        if (this.onReset) {
            this.onReset({
                items: this.items
            });
        }

        if (this.afterFilter) {
            this.afterFilter({
                values: this.collectValues(),
                items: this.items,
                visibleCount: this.items.length,
                reset: true
            });
        }
    };

    global.AdminSearch = {
        init: function (options) {
            return new AdminSearch(options || {});
        }
    };
})(window);

